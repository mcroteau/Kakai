package io.kakai.web;

import com.sun.net.httpserver.HttpExchange;
import io.kakai.exception.KakaiException;
import io.kakai.implement.ViewRenderer;
import io.kakai.model.web.*;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExperienceProcessor {

    final Integer ZERO = 0;
    final Integer ONE  = 1;

    final String DOT      = "\\.";
    final String NEWLINE  = "\n";
    final String LOCATOR  = "\\$\\{[a-zA-Z+\\.+\\(\\)]*\\}";
    final String FOREACH  = "<kakai:iterate";
    final String ENDEACH  = "</kakai:iterate>";
    final String IFSPEC   = "<kakai:if";
    final String ENDIF    = "</kakai:if>";
    final String SETVAR   = "<kakai:set";
    final String OPENSPEC = "<kakai:if spec=\"${";
    final String ENDSPEC  = "}";

    final String COMMENT      = "<%--";
    final String HTML_COMMENT = "<!--";

    //todo:her, please.
    public String execute(Map<String, ViewRenderer> viewRenderers, String pageElements, HttpResponse resp, HttpRequest req, HttpExchange exchange) throws KakaiException, NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        setHttpSessionResponseValues(req, resp);

        List<String> elementEntries = Arrays.asList(pageElements.split("\n"));
        List<String> renderedElementEntries = getInterpretedRenderers(req, exchange, elementEntries, viewRenderers);

        List<DataPartial> dataPartials = getConvertedDataPartials(renderedElementEntries);
        List<DataPartial> dataPartialsInflated = getInflatedPartials(dataPartials, resp);

        List<DataPartial> dataPartialsComplete = getCompletedPartials(dataPartialsInflated, resp);
        List<DataPartial> dataPartialsCompleteReady = getNilElementEntryPartials(dataPartialsComplete);

        StringBuilder pageElementsComplete = getElementsCompleted(dataPartialsCompleteReady);
        return pageElementsComplete.toString();
    }

    List<DataPartial> getNilElementEntryPartials(List<DataPartial> dataPartialsComplete) {
        List<DataPartial> dataPartialsCompleteReady = new ArrayList();
        for(DataPartial dataPartial : dataPartialsComplete){
            String elementEntry = dataPartial.getEntry();
            Pattern pattern = Pattern.compile(LOCATOR);
            Matcher matcher = pattern.matcher(elementEntry);
            while (matcher.find()) {
                String element = matcher.group();
                String regexElement = element
                        .replace("${", "\\$\\{")
                        .replace("}", "\\}")
                        .replace(".", "\\.");
                String elementEntryComplete = elementEntry.replaceAll(regexElement, "");
                dataPartial.setEntry(elementEntryComplete);
            }
            dataPartialsCompleteReady.add(dataPartial);
        }
        return dataPartialsCompleteReady;
    }

    void setHttpSessionResponseValues(HttpRequest req, HttpResponse resp) {
        if(req == null || req.getSession() == null)return;
        Map<String, Object> sessionHashMap = req.getSession().data();
        for(Map.Entry<String, Object> entry : sessionHashMap.entrySet()){
            String key = entry.getKey();
            Object value = entry.getValue();
            resp.set(key, value);
        }
    }

    StringBuilder getElementsCompleted(List<DataPartial> dataPartialsComplete) {
        StringBuilder builder = new StringBuilder();
        for(DataPartial dataPartial: dataPartialsComplete) {
            if(!dataPartial.getEntry().equals("") &&
                    !dataPartial.getEntry().contains(SETVAR)){
                builder.append(dataPartial.getEntry() + NEWLINE);
            }
        }
        return builder;
    }

    void setResponseVariable(String baseEntry, HttpResponse resp) {
        int startVariableIdx = baseEntry.indexOf("var=");
        int endVariableIdx = baseEntry.indexOf("\"", startVariableIdx + 5);
        String variableEntry = baseEntry.substring(startVariableIdx + 5, endVariableIdx);
        int startValueIdx = baseEntry.indexOf("val=");
        int endValueIdx = baseEntry.indexOf("\"", startValueIdx + 5);
        String valueEntry = baseEntry.substring(startValueIdx + 5, endValueIdx);
        resp.set(variableEntry, valueEntry);
    }

    List<DataPartial> getConvertedDataPartials(List<String> elementEntries) {
        List<DataPartial> dataPartials = new ArrayList<>();
        for (String elementEntry : elementEntries) {
            if(elementEntry.contains(COMMENT) ||
                    elementEntry.contains(HTML_COMMENT))continue;
            DataPartial dataPartial = new DataPartial();
            dataPartial.setEntry(elementEntry);
            if (elementEntry.contains(this.FOREACH)) {
                dataPartial.setIterable(true);
            }else if(elementEntry.contains(this.IFSPEC)) {
                dataPartial.setSpec(true);
            }else if(elementEntry.contains(this.ENDEACH)){
                dataPartial.setEndIterable(true);
            }else if(elementEntry.contains(this.ENDIF)){
                dataPartial.setEndSpec(true);
            }else if(elementEntry.contains(SETVAR)){
                dataPartial.setSetVar(true);
            }
            dataPartials.add(dataPartial);
        }
        return dataPartials;
    }

    List<String> getInterpretedRenderers(HttpRequest req, HttpExchange exchange, List<String> elementEntries, Map<String, ViewRenderer> viewRenderers){

        for(Map.Entry<String, ViewRenderer> viewRendererEntry : viewRenderers.entrySet()){
            ViewRenderer viewRenderer = viewRendererEntry.getValue();

            String rendererKey = viewRenderer.getKey();//dice:rollem in <dice:rollem> is key

            String openRendererKey = "<" + rendererKey + ">";
            String completeRendererKey = "<" + rendererKey + "/>";
            String endRendererKey = "</" + rendererKey + ">";

            for(int tao = 0; tao < elementEntries.size(); tao++) {

                String elementEntry = elementEntries.get(tao);

                if (viewRenderer.isEval() &&
                        (elementEntry.contains(openRendererKey)) &&
                        viewRenderer.truthy(req, exchange)) {

                    viewRendererIteration:
                    for(int moa = tao; moa < elementEntries.size(); moa++){
                        String elementEntryDeux = elementEntries.get(moa);
                        elementEntries.set(moa, elementEntryDeux);
                        if(elementEntryDeux.contains(endRendererKey))break viewRendererIteration;
                    }
                }
                if (viewRenderer.isEval() &&
                        (elementEntry.contains(openRendererKey)) &&
                        !viewRenderer.truthy(req, exchange)) {
                    viewRendererIteration:
                    for(int moa = tao; moa < elementEntries.size(); moa++){
                        String elementEntryDeux = elementEntries.get(moa);
                        elementEntries.set(moa, "");
                        if(elementEntryDeux.contains(endRendererKey))break viewRendererIteration;
                    }
                }
                if(!viewRenderer.isEval() &&
                        elementEntry.contains(completeRendererKey)){
                    String rendered = viewRenderer.render(req, exchange);
                    elementEntries.set(tao, rendered);
                }
            }
        }
        return elementEntries;
    }

    List<DataPartial> getCompletedPartials(List<DataPartial> dataPartialsPre, HttpResponse resp) throws InvocationTargetException, NoSuchMethodException, KakaiException, NoSuchFieldException, IllegalAccessException {

        List<DataPartial> dataPartials = new ArrayList<>();
        for(DataPartial dataPartial : dataPartialsPre) {

            if (!dataPartial.getSpecPartials().isEmpty()) {
                boolean passesRespSpecsIterations = true;
                boolean passesObjectSpecsIterations = true;
                specIteration:
                for(DataPartial specPartial : dataPartial.getSpecPartials()) {
                    if(!dataPartial.getComponents().isEmpty()){
                        for(ObjectComponent objectComponent : dataPartial.getComponents()) {
                            Object object = objectComponent.getObject();
                            String activeField = objectComponent.getActiveField();
                            if (!passesSpec(object, activeField, specPartial, dataPartial, resp)) {
                                passesObjectSpecsIterations = false;
                                break specIteration;
                            }
                        }
                    }else{
                        if (!passesSpec(specPartial, resp)){
                            passesRespSpecsIterations = false;
                        }
                    }
                }

                if(dataPartial.getComponents().isEmpty()) {
                    if (passesRespSpecsIterations) {
                        String entryBase = dataPartial.getEntry();
                        if (!dataPartial.isSetVar()) {
                            List<LineComponent> lineComponents = getPageLineComponents(entryBase);
                            String entryBaseComplete = getCompleteLineElementResponse(entryBase, lineComponents, resp);
                            DataPartial completePartial = new DataPartial(entryBaseComplete);
                            dataPartials.add(completePartial);
                        } else {
                            setResponseVariable(entryBase, resp);
                        }
                    }
                }else {
                    if (passesObjectSpecsIterations) {
                        if (!dataPartial.getComponents().isEmpty()) {
                            String entryBase = dataPartial.getEntry();
                            for (ObjectComponent objectComponent : dataPartial.getComponents()) {
                                Object object = objectComponent.getObject();
                                String activeField = objectComponent.getActiveField();
                                if (!dataPartial.isSetVar()) {
                                    entryBase = getCompleteLineElementObject(activeField, object, entryBase, resp);
                                } else {
                                    setResponseVariable(entryBase, resp);
                                }
                            }
                            DataPartial completePartial = new DataPartial(entryBase);
                            dataPartials.add(completePartial);
                        }
                    }
                }

            }else if(dataPartial.isWithinIterable()){
                String entryBase = dataPartial.getEntry();
                if(!dataPartial.isSetVar()) {
                    String entryBaseComplete = getCompleteInflatedDataPartial(dataPartial, resp);
                    DataPartial completePartial = new DataPartial(entryBaseComplete);
                    dataPartials.add(completePartial);
                }else{
                    setResponseVariable(entryBase, resp);
                }
            }else{
                String entryBase = dataPartial.getEntry();
                if(!dataPartial.isSetVar()) {
                    List<LineComponent> lineComponents = getPageLineComponents(entryBase);
                    String entryBaseComplete = getCompleteLineElementResponse(entryBase, lineComponents, resp);
                    DataPartial completePartial = new DataPartial(entryBaseComplete);
                    dataPartials.add(completePartial);
                }else{
                    setResponseVariable(entryBase, resp);
                }
            }
        }
        return dataPartials;
    }

    String getCompleteLineElementObject(String activeField, Object object, String entryBase, HttpResponse resp) throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        List<LineComponent> lineComponents = getPageLineComponents(entryBase);
        List<LineComponent> iteratedLineComponents = new ArrayList<>();
        for(LineComponent lineComponent : lineComponents){
            if(activeField.equals(lineComponent.getActiveField())) {
                String objectField = lineComponent.getObjectField();
                String objectValue = getObjectValueForLineComponent(objectField, object);
                if(objectValue != null){
                    String lineElement = lineComponent.getCompleteLineElement();
                    entryBase = entryBase.replaceAll(lineElement, objectValue);
                    lineComponent.setIterated(true);
                }else{
                    lineComponent.setIterated(false);
                }
            }
            iteratedLineComponents.add(lineComponent);
        }

        String entryBaseComplete = getCompleteLineElementResponse(entryBase, iteratedLineComponents, resp);
        return entryBaseComplete;
    }

    String getCompleteInflatedDataPartial(DataPartial dataPartial, HttpResponse resp) throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        String entryBase = dataPartial.getEntry();
        List<LineComponent> lineComponents = getPageLineComponents(entryBase);
        List<LineComponent> iteratedLineComponents = new ArrayList<>();
        for(ObjectComponent objectComponent : dataPartial.getComponents()) {
            Object object = objectComponent.getObject();
            String activeField = objectComponent.getActiveField();

            for(LineComponent lineComponent : lineComponents){
                if(activeField.equals(lineComponent.getActiveField())) {
                    String objectField = lineComponent.getObjectField();
                    String objectValue = getObjectValueForLineComponent(objectField, object);
                    if(objectValue != null){
                        String lineElement = lineComponent.getCompleteLineElement();
                        entryBase = entryBase.replaceAll(lineElement, objectValue);
                        lineComponent.setIterated(true);
                    }else{
                        lineComponent.setIterated(false);
                    }
                }
                iteratedLineComponents.add(lineComponent);
            }
        }

        String entryBaseComplete = getCompleteLineElementResponse(entryBase, iteratedLineComponents, resp);
        return entryBaseComplete;
    }

    String getCompleteLineElementResponse(String entryBase, List<LineComponent> lineComponents, HttpResponse resp) throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        for(LineComponent lineComponent: lineComponents){
            String activeObjectField = lineComponent.getActiveField();
            String objectField = lineComponent.getObjectField();
            String objectValue = getResponseValueLineComponent(activeObjectField, objectField, resp);
            String objectValueClean = objectValue != null ? objectValue.replace("${", "\\$\\{").replace("}", "\\}") : "";
            if(objectValue != null && objectField.contains("()")){
                String lineElement = lineComponent.getCompleteLineFunction();
                entryBase = entryBase.replaceAll(lineElement, objectValue);
            }else if(objectValue != null && !objectValue.contains("()")){
                String lineElement = lineComponent.getCompleteLineElement();
                entryBase = entryBase.replaceAll(lineElement, objectValueClean);
            }
        }
        return entryBase;
    }


    List<DataPartial> getInflatedPartials(List<DataPartial> dataPartials, HttpResponse resp) throws NoSuchFieldException, IllegalAccessException, KakaiException {

        List<DataPartial> dataPartialsPre = new ArrayList<>();
        for(int tao = 0; tao < dataPartials.size(); tao++) {
            DataPartial dataPartial = dataPartials.get(tao);
            String basicEntry = dataPartial.getEntry();

            DataPartial dataPartialSies = new DataPartial();
            dataPartialSies.setEntry(basicEntry);
            dataPartialSies.setSetVar(dataPartial.isSetVar());

            if(dataPartial.isIterable()) {
                dataPartialSies.setWithinIterable(true);

                IterableResult iterableResult = getIterableResult(basicEntry, resp);
                List<DataPartial> iterablePartials = getIterablePartials(tao + 1, dataPartials);

                List<Object> objects = iterableResult.getMojos();//renaming variables
                for(int foo = 0; foo < objects.size(); foo++){
                    Object object = objects.get(foo);

                    ObjectComponent component = new ObjectComponent();
                    component.setActiveField(iterableResult.getField());
                    component.setObject(object);

                    List<ObjectComponent> objectComponents = new ArrayList<>();
                    objectComponents.add(component);

                    for(int beta = 0; beta < iterablePartials.size(); beta++){
                        DataPartial dataPartialDeux = iterablePartials.get(beta);
                        String basicEntryDeux = dataPartialDeux.getEntry();

                        DataPartial dataPartialCinq = new DataPartial();
                        dataPartialCinq.setEntry(basicEntryDeux);
                        dataPartialCinq.setWithinIterable(true);
                        dataPartialCinq.setComponents(objectComponents);
                        dataPartialCinq.setField(iterableResult.getField());
                        dataPartialCinq.setSetVar(dataPartialDeux.isSetVar());

                        if(dataPartialDeux.isIterable()) {

                            IterableResult iterableResultDeux = getIterableResultNested(basicEntryDeux, object);
                            List<DataPartial> iterablePartialsDeux = getIterablePartialsNested(beta + 1, iterablePartials);

                            List<Object> pojosDeux = iterableResultDeux.getMojos();

                            for(int tai = 0; tai < pojosDeux.size(); tai++){
                                Object objectDeux = pojosDeux.get(tai);

                                ObjectComponent componentDeux = new ObjectComponent();
                                componentDeux.setActiveField(iterableResult.getField());
                                componentDeux.setObject(object);

                                ObjectComponent componentTrois = new ObjectComponent();
                                componentTrois.setActiveField(iterableResultDeux.getField());
                                componentTrois.setObject(objectDeux);

                                List<ObjectComponent> objectComponentsDeux = new ArrayList<>();
                                objectComponentsDeux.add(componentDeux);
                                objectComponentsDeux.add(componentTrois);

                                for (int chi = 0; chi < iterablePartialsDeux.size(); chi++) {
                                    DataPartial dataPartialTrois = iterablePartialsDeux.get(chi);
                                    DataPartial dataPartialQuatre = new DataPartial();
                                    dataPartialQuatre.setEntry(dataPartialTrois.getEntry());
                                    dataPartialQuatre.setWithinIterable(true);
                                    dataPartialQuatre.setComponents(objectComponentsDeux);
                                    dataPartialQuatre.setField(iterableResultDeux.getField());
                                    dataPartialQuatre.setSetVar(dataPartialTrois.isSetVar());

                                    if(!isPeripheralPartial(dataPartialTrois)) {
                                        List<DataPartial> specPartials = getSpecPartials(dataPartialTrois, dataPartials);
                                        dataPartialQuatre.setSpecPartials(specPartials);
                                        dataPartialsPre.add(dataPartialQuatre);
                                    }
                                }
                            }
                        }else if(!isPeripheralPartial(dataPartialDeux) &&
                                (isTrailingPartialNested(beta, iterablePartials) || !withinIterable(dataPartialDeux, iterablePartials))){
                            List<DataPartial> specPartials = getSpecPartials(dataPartialDeux, dataPartials);
                            dataPartialCinq.setSpecPartials(specPartials);
                            dataPartialsPre.add(dataPartialCinq);
                        }
                    }
                }

            }else if(!isPeripheralPartial(dataPartial) &&
                    (isTrailingPartial(tao, dataPartials) || !withinIterable(dataPartial, dataPartials))){

                List<DataPartial> specPartials = getSpecPartials(dataPartial, dataPartials);
                dataPartialSies.setSpecPartials(specPartials);
                dataPartialSies.setWithinIterable(false);
                dataPartialsPre.add(dataPartialSies);
            }
        }

        return dataPartialsPre;
    }

    boolean isTrailingPartialNested(int tao, List<DataPartial> dataPartials) {
        Integer openCount = 0, endCount = 0, endIdx = 0;
        for(int tai = 0; tai < dataPartials.size(); tai++){
            DataPartial dataPartial = dataPartials.get(tai);
            if(dataPartial.isIterable())openCount++;
            if(dataPartial.isEndIterable()){
                endCount++;
                endIdx = tai;
            }
        }
        if(openCount == 1 && openCount == endCount && tao > endIdx)return true;
        return false;
    }

    boolean isTrailingPartial(int chi, List<DataPartial> dataPartials) {
        Integer openCount = 0, endCount = 0, endIdx = 0;
        for(int tai = 0; tai < dataPartials.size(); tai++){
            DataPartial dataPartial = dataPartials.get(tai);
            if(dataPartial.isIterable())openCount++;
            if(dataPartial.isEndIterable()){
                endCount++;
                endIdx = tai;
            }
        }
        if(openCount != 0 && chi > endIdx)return true;
        return false;
    }

    boolean isPeripheralPartial(DataPartial dataPartial) {
        return dataPartial.isSpec() || dataPartial.isIterable() || dataPartial.isEndSpec() || dataPartial.isEndIterable();
    }

    List<DataPartial> getSpecPartials(DataPartial dataPartialLocator, List<DataPartial> dataPartials) {
        Set<DataPartial> specPartials = new HashSet<>();
        for(int tao = 0; tao < dataPartials.size(); tao++){
            boolean matchDiscovered = false;
            int openCount = 0; int endCount = 0;
            DataPartial dataPartial = dataPartials.get(tao);
            if(dataPartial.isSpec()) {
                openCount++;
                matchIteration:
                for (int chao = tao; chao < dataPartials.size(); chao++) {
                    DataPartial dataPartialDeux = dataPartials.get(chao);
                    if (dataPartialDeux.getGuid().equals(dataPartial.getGuid())) continue matchIteration;
                    if (dataPartialLocator.getGuid().equals(dataPartialDeux.getGuid())) {
                        break matchIteration;
                    }
                    if (dataPartialDeux.isEndSpec()) {
                        endCount++;
                    }
                    if (dataPartialDeux.isSpec()) {
                        openCount++;
                    }
                    if(openCount == endCount)break matchIteration;
                }
            }
            if(dataPartialLocator.getGuid().equals(dataPartial.getGuid()))break;

            if(openCount > endCount)specPartials.add(dataPartial);
        }
        List<DataPartial> specPartialsReady = new ArrayList(specPartials);

        return specPartialsReady;
    }

    boolean passesSpec(Object object, String activeField, DataPartial specPartial, DataPartial dataPartial, HttpResponse resp) throws NoSuchMethodException, KakaiException, IllegalAccessException, NoSuchFieldException, InvocationTargetException {
        if(dataPartial.isWithinIterable() && passesIterableSpec(activeField, specPartial, object, resp)){
            return true;
        }
        if(!dataPartial.isWithinIterable() && passesSpec(specPartial, resp)){
            return true;
        }
        return false;
    }

    List<DataPartial> getIterablePartials(int openIdx, List<DataPartial> dataPartials) throws KakaiException {
        Integer openCount = 1, endCount = 0;
        List<DataPartial> dataPartialsDeux = new ArrayList<>();
        for (int foo = openIdx; foo < dataPartials.size(); foo++) {
            DataPartial dataPartial = dataPartials.get(foo);

            if(dataPartial.isIterable())openCount++;
            if(dataPartial.isEndIterable())endCount++;

            if(openCount != 0 && openCount == endCount)break;

            dataPartialsDeux.add(dataPartial);
        }
        return dataPartialsDeux;
    }

    List<DataPartial> getIterablePartialsNested(int openIdx, List<DataPartial> dataPartials) throws KakaiException {
        List<DataPartial> dataPartialsDeux = new ArrayList<>();
        Integer endIdx = getEndEach(openIdx, dataPartials);
        for (int foo = openIdx; foo < endIdx; foo++) {
            DataPartial basePartial = dataPartials.get(foo);
            dataPartialsDeux.add(basePartial);
        }
        return dataPartialsDeux;
    }

    int getEndEach(int openIdx, List<DataPartial> basePartials) throws KakaiException {
        Integer openEach = 1;
        Integer endEach = 0;
        for (int qxro = openIdx + 1; qxro < basePartials.size(); qxro++) {
            DataPartial basePartial = basePartials.get(qxro);
            String basicEntry = basePartial.getEntry();
            if(basicEntry.contains(this.ENDEACH))endEach++;

            if(openEach > 3)throw new KakaiException("too many nested <kakai:iterate>.");
            if(basicEntry.contains(this.ENDEACH) && endEach == openEach && endEach != 0){
                return qxro + 1;
            }
        }
        throw new KakaiException("missing end </kakai:iterate>");
    }


    boolean withinIterable(DataPartial dataPartial, List<DataPartial> dataPartials){
        int openCount = 0, endCount = 0;
        for(DataPartial it : dataPartials){
            if(it.isIterable())openCount++;
            if(it.isEndIterable())endCount++;
            if(it.getGuid().equals(dataPartial.getGuid()))break;
        }
        if(openCount == 1 && endCount == 0)return true;
        if(openCount == 2 && endCount == 1)return true;
        if(openCount == 2 && endCount == 0)return true;
        return false;
    }


    boolean passesIterableSpec(String activeField, DataPartial specPartial, Object activeObject, HttpResponse resp) throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {

        String specElementEntry = specPartial.getEntry();
        int startExpression = specElementEntry.indexOf(OPENSPEC);
        int endExpression = specElementEntry.indexOf(ENDSPEC);
        String expressionElement = specElementEntry.substring(startExpression + OPENSPEC.length(), endExpression);
        String conditionalElement = getConditionalElement(expressionElement);

        if(conditionalElement.equals(""))return false;

        String[] expressionElements = expressionElement.split(conditionalElement);
        String subjectElement = expressionElements[ZERO].trim();

        String[] subjectFieldElements = subjectElement.split(DOT, 2);
        String activeSubjectFieldElement = subjectFieldElements[ZERO];
        String activeSubjectFieldsElement = subjectFieldElements[ONE];

        String predicateElement = expressionElements[ONE].trim();
        Object activeSubjectObject = activeObject;

        if(activeSubjectFieldsElement.contains("()")){
            String activeMethodName = activeSubjectFieldsElement.replace("()", "");
            Object activeMethodObject = resp.get(activeSubjectFieldElement);
            if(activeMethodObject == null)return false;
            Method activeMethod = activeMethodObject.getClass().getMethod(activeMethodName);
            activeMethod.setAccessible(true);
            Object activeObjectValue = activeMethod.invoke(activeMethodObject);
            if(activeObjectValue == null)return false;
            String subjectValue = String.valueOf(activeObjectValue);
            Integer subjectNumericValue = Integer.parseInt(subjectValue);
            String predicateValue = predicateElement.replaceAll("'", "");
            Integer predicateNumericValue = Integer.parseInt(predicateValue);
            boolean passesSpecification = getValidation(subjectNumericValue, predicateNumericValue, conditionalElement, expressionElement);
            return passesSpecification;
        }else{
            String[] activeSubjectFieldElements = activeSubjectFieldsElement.split(DOT);
            for(String activeFieldElement : activeSubjectFieldElements){
                activeSubjectObject = getObjectValue(activeFieldElement, activeSubjectObject);
            }
        }

        String subjectValue = String.valueOf(activeSubjectObject);

        if(predicateElement.contains(".")){

            String[] predicateFieldElements = predicateElement.split(DOT, 2);
            String predicateField = predicateFieldElements[ZERO];
            String activePredicateFields = predicateFieldElements[ONE];

            String[] activePredicateFieldElements = activePredicateFields.split(DOT);
            Object activePredicateObject = resp.get(predicateField);
            for(String activeFieldElement : activePredicateFieldElements){
                activePredicateObject = getObjectValue(activeFieldElement, activePredicateObject);
            }

            String predicateValue = String.valueOf(activePredicateObject);
            boolean passesSpecification = passesSpec(subjectValue, predicateValue, conditionalElement);
            return passesSpecification;

        }else if(!predicateElement.contains("'")){
            Object activePredicateObject = resp.get(predicateElement);
            String predicateValue = String.valueOf(activePredicateObject);
            boolean passesSpecification = passesSpec(subjectValue, predicateValue, conditionalElement);
            return passesSpecification;
        }

        String predicateValue = predicateElement.replaceAll("'", "");
        boolean passesSpecification = passesSpec(subjectValue, predicateValue, conditionalElement);
        return passesSpecification;
    }

    boolean passesSpec(DataPartial specPartial, HttpResponse resp) throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        String specElementEntry = specPartial.getEntry();
        int startExpression = specElementEntry.indexOf(OPENSPEC);
        int endExpression = specElementEntry.indexOf(ENDSPEC);
        String expressionElement = specElementEntry.substring(startExpression + OPENSPEC.length(), endExpression);
        String conditionalElement = getConditionalElement(expressionElement);

        String subjectElement = expressionElement, predicateValue = "";
        if(!conditionalElement.equals("")){
            String[] expressionElements = expressionElement.split(conditionalElement);
            subjectElement = expressionElements[ZERO].trim();
            String predicateElement = expressionElements[ONE];
            predicateValue = predicateElement.replaceAll("'", "").trim();
        }

        if(subjectElement.contains(".")){

            if(predicateValue.equals("") &&
                    conditionalElement.equals("")){
                boolean falseActive = subjectElement.contains("!");
                String subjectElementClean = subjectElement.replace("!", "");

                String[] subjectFieldElements = subjectElementClean.split(DOT, 2);
                String subjectField = subjectFieldElements[ZERO];
                String subjectFieldElementsRemainder = subjectFieldElements[ONE];

                Object activeSubjectObject = resp.get(subjectField);
                if(activeSubjectObject == null)return false;

                String[] activeSubjectFieldElements = subjectFieldElementsRemainder.split(DOT);
                for(String activeFieldElement : activeSubjectFieldElements){
                    activeSubjectObject = getObjectValue(activeFieldElement, activeSubjectObject);
                }

                boolean activeSubjectObjectBoolean = (Boolean) activeSubjectObject;
                if(activeSubjectObjectBoolean && !falseActive)return true;
                if(!activeSubjectObjectBoolean && falseActive)return true;
            }

            if(subjectElement.contains("()")){
                String subjectElements = subjectElement.replace("()", "");
                String[] subjectFieldElements = subjectElements.split(DOT);
                String subjectField = subjectFieldElements[ZERO];
                String methodName = subjectFieldElements[ONE];
                Object activeSubjectObject = resp.get(subjectField);
                if(activeSubjectObject == null)return false;
                Method activeMethod = activeSubjectObject.getClass().getMethod(methodName);
                activeMethod.setAccessible(true);
                Object activeObjectValue = activeMethod.invoke(activeSubjectObject);
                if(activeObjectValue == null)return false;
                String subjectValue = String.valueOf(activeObjectValue);
                Integer subjectNumericValue = Integer.parseInt(subjectValue);
                Integer predicateNumericValue = Integer.parseInt(predicateValue);
                boolean passesSpecification = getValidation(subjectNumericValue, predicateNumericValue, conditionalElement, expressionElement);
                return passesSpecification;
            }

            String[] subjectFieldElements = subjectElement.split(DOT, 2);
            String subjectField = subjectFieldElements[ZERO];
            String activeSubjectFields = subjectFieldElements[ONE];

            String[] activeSubjectFieldElements = activeSubjectFields.split(DOT);
            Object activeSubjectObject = resp.get(subjectField);
            if(activeSubjectObject == null)return false;

            for(String activeFieldElement : activeSubjectFieldElements){
                activeSubjectObject = getObjectValue(activeFieldElement, activeSubjectObject);
            }

            if(activeSubjectObject == null){
                boolean passesSpecification = passesNilSpec(activeSubjectObject, predicateValue, conditionalElement);
                return passesSpecification;
            }

            String subjectValue = String.valueOf(activeSubjectObject);
            boolean passesSpecification = passesSpec(subjectValue, predicateValue, conditionalElement);
            return passesSpecification;
        }else if(predicateValue.equals("") &&
                conditionalElement.equals("")){
            boolean falseActive = subjectElement.contains("!");
            String subjectElementClean = subjectElement.replace("!", "");
            Object activeSubjectObject = resp.get(subjectElementClean);
            if(activeSubjectObject == null) return false;
            boolean activeSubjectObjectBoolean = (Boolean) activeSubjectObject;
            if(activeSubjectObjectBoolean && !falseActive)return true;
            if(!activeSubjectObjectBoolean && falseActive)return true;
        }

        Object activeSubjectObject = resp.get(subjectElement);
        if(activeSubjectObject == null){
            boolean passesSpecification = passesNilSpec(activeSubjectObject, predicateValue, conditionalElement);
            return passesSpecification;
        }
        String subjectValue = String.valueOf(activeSubjectObject).trim();
        boolean passesSpecification = passesSpec(subjectValue, predicateValue, conditionalElement);
        return passesSpecification;
    }

    boolean passesNilSpec(Object activePredicateObject, String predicateValue, String conditionalElement) {
        if(activePredicateObject == null && predicateValue.equals("null") && conditionalElement.equals("=="))return true;
        if(activePredicateObject == null && predicateValue.equals("null") && conditionalElement.equals("!="))return false;
        if(activePredicateObject == null && predicateValue.equals("") && conditionalElement.equals("=="))return true;
        if(activePredicateObject == null && predicateValue.equals("") && conditionalElement.equals(""))return false;
        if(activePredicateObject == null && predicateValue.equals("") && conditionalElement.equals("!="))return false;
        return false;
    }

    boolean passesSpec(String subjectValue, String predicateValue, String conditionalElement) {
        if(subjectValue.equals(predicateValue) && conditionalElement.equals("=="))return true;
        if(!subjectValue.equals(predicateValue) && conditionalElement.equals("!="))return true;
        return false;
    }

    Boolean getValidation(Integer subjectValue, Integer predicateValue, String condition, String expressionElement){
        if(condition.equals(">")) {
            if(subjectValue > predicateValue)return true;
        }else if (condition.equals("==")) {
            if(subjectValue.equals(predicateValue))return true;
        }
        return false;
    }

    String getConditionalElement(String expressionElement){
        if(expressionElement.contains(">"))return ">";
        if(expressionElement.contains("<"))return "<";
        if(expressionElement.contains("=="))return "==";
        if(expressionElement.contains(">="))return ">=";
        if(expressionElement.contains("<="))return "<=";
        if(expressionElement.contains("!="))return "!=";
        return "";
    }

    IterableResult getIterableResultNested(String entry, Object mojo) throws KakaiException, NoSuchFieldException, IllegalAccessException {
        int startEach = entry.indexOf("<kakai:iterate");

        int startIterate = entry.indexOf("items=", startEach + 1);
        int endIterate = entry.indexOf("\"", startIterate + 7);//items="
        String iterableKey = entry.substring(startIterate + 9, endIterate -1 );//items="${ and }

        String iterablePadded = "${" + iterableKey + "}";

        int startField = iterablePadded.indexOf(".");
        int endField = iterablePadded.indexOf("}", startField);
        String field = iterablePadded.substring(startField + 1, endField);

        int startItem = entry.indexOf("var=", endIterate);
        int endItem = entry.indexOf("\"", startItem + 5);//var="
        String activeField = entry.substring(startItem + 5, endItem);

        List<Object> mojos = (ArrayList) getIterableValueRecursive(0, field, mojo);
        IterableResult iterableResult = new IterableResult();
        iterableResult.setField(activeField);
        iterableResult.setMojos(mojos);
        return iterableResult;
    }

    private IterableResult getIterableResult(String entry, HttpResponse httpResponse) throws NoSuchFieldException, IllegalAccessException {

        int startEach = entry.indexOf("<kakai:iterate");

        int startIterate = entry.indexOf("items=", startEach);
        int endIterate = entry.indexOf("\"", startIterate + 7);//items=".
        String iterableKey = entry.substring(startIterate + 9, endIterate -1 );//items="${ }

        int startItem = entry.indexOf("var=", endIterate);
        int endItem = entry.indexOf("\"", startItem + 6);//items="
        String activeField = entry.substring(startItem + 5, endItem);

        String expression = entry.substring(startIterate + 7, endIterate);

        List<Object> pojos = new ArrayList<>();
        if(iterableKey.contains(".")){
            pojos = getIterableInitial(expression, httpResponse);
        }else if(httpResponse.data().containsKey(iterableKey)){
            pojos = (ArrayList) httpResponse.get(iterableKey);
        }

        IterableResult iterableResult = new IterableResult();
        iterableResult.setField(activeField);
        iterableResult.setMojos(pojos);
        return iterableResult;
    }

    private List<Object> getIterableInitial(String expression, HttpResponse httpResponse) throws NoSuchFieldException, IllegalAccessException {
        int startField = expression.indexOf("${");
        int endField = expression.indexOf(".", startField);
        String key = expression.substring(startField + 2, endField);
        if(httpResponse.data().containsKey(key)){
            Object obj = httpResponse.get(key);
            Object objList = getIterableRecursive(expression, obj);
            return (ArrayList) objList;
        }
        return new ArrayList<>();
    }

    private List<Object> getIterableRecursive(String expression, Object objBase) throws NoSuchFieldException, IllegalAccessException {
        List<Object> objs = new ArrayList<>();
        int startField = expression.indexOf(".");
        int endField = expression.indexOf("}");

        String field = expression.substring(startField + 1, endField);
        Object obj = getValueRecursive(0, field, objBase);

        if(obj != null){
            return (ArrayList) obj;
        }
        return objs;
    }

    private Object getIterableValueRecursive(int idx, String baseField, Object baseObj) throws NoSuchFieldException, IllegalAccessException {
        String[] fields = baseField.split("\\.");
        if(fields.length > 1){
            idx++;
            String key = fields[0];
            Field fieldObj = baseObj.getClass().getDeclaredField(key);
            if(fieldObj != null){
                fieldObj.setAccessible(true);
                Object obj = fieldObj.get(baseObj);
                int start = baseField.indexOf(".");
                String fieldPre = baseField.substring(start + 1);
                if(obj != null) {
                    return getValueRecursive(idx, fieldPre, obj);
                }
            }
        }else{
            Field fieldObj = baseObj.getClass().getDeclaredField(baseField);
            if(fieldObj != null) {
                fieldObj.setAccessible(true);
                Object obj = fieldObj.get(baseObj);
                if (obj != null) {
                    return obj;
                }
            }
        }
        return new ArrayList();
    }

    private Object getValueRecursive(int idx, String baseField, Object baseObj) throws NoSuchFieldException, IllegalAccessException {
        String[] fields = baseField.split("\\.");
        if(fields.length > 1){
            idx++;
            String key = fields[0];
            Field fieldObj = baseObj.getClass().getDeclaredField(key);
            fieldObj.setAccessible(true);
            Object obj = fieldObj.get(baseObj);
            int start = baseField.indexOf(".");
            String fieldPre = baseField.substring(start + 1);
            if(obj != null) {
                return getValueRecursive(idx, fieldPre, obj);
            }

        }else{
            try {
                Field fieldObj = baseObj.getClass().getDeclaredField(baseField);
                fieldObj.setAccessible(true);
                Object obj = fieldObj.get(baseObj);
                if (obj != null) {
                    return obj;
                }
            }catch(Exception ex){}
        }
        return null;
    }


    List<LineComponent> getPageLineComponents(String pageElementEntry){
        List<LineComponent> lineComponents = new ArrayList<>();
        Pattern pattern = Pattern.compile(LOCATOR);
        Matcher matcher = pattern.matcher(pageElementEntry);
        while (matcher.find()) {
            LineComponent lineComponent = new LineComponent();
            String lineElement = matcher.group();
            String cleanElement = lineElement
                    .replace("${", "")
                    .replace("}", "");
            String activeField = cleanElement;
            String objectField = "";
            if(cleanElement.contains(".")) {
                String[] elements = cleanElement.split("\\.", 2);
                activeField = elements[0];
                objectField = elements[1];
            }
            lineComponent.setActiveField(activeField);
            lineComponent.setObjectField(objectField);
            lineComponent.setLineElement(cleanElement);
            lineComponents.add(lineComponent);
        }

        return lineComponents;
    }

    String getResponseValueLineComponent(String activeField, String objectField, HttpResponse resp) throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {

        if(objectField.contains(".")){

            Object activeObject = resp.get(activeField);
            if(activeObject != null) {
                String[] activeObjectFields = objectField.split(DOT);

                for (String activeObjectField : activeObjectFields) {
                    activeObject = getObjectValue(activeObjectField, activeObject);
                }

                if (activeObject == null) return null;
                return String.valueOf(activeObject);
            }
        }else{

            Object respValue = resp.get(activeField);
            if(respValue != null &&
                    !objectField.equals("") &&
                        !objectField.contains(".")) {

                Object objectValue = null;
                if(objectField.contains("()")){
                    String methodName = objectField.replace("()", "");
                    Method methodObject = respValue.getClass().getDeclaredMethod(methodName);
                    methodObject.setAccessible(true);
                    if(methodObject != null) {
                        objectValue = methodObject.invoke(respValue);
                    }
                }else{
                    objectValue = getObjectValue(objectField, respValue);
                }

                if (objectValue == null) return null;
                return String.valueOf(objectValue);
            }else{
                if (respValue == null) return null;
                return String.valueOf(respValue);
            }
        }
        return null;
    }

    String getObjectValueForLineComponent(String objectField, Object object) throws IllegalAccessException, NoSuchFieldException {

        if(objectField.contains(".")){
            String[] objectFields = objectField.split("\\.");

            Object activeObject = object;
            for(String activeObjectField : objectFields){
                activeObject = getObjectValue(activeObjectField, activeObject);
            }

            if(activeObject == null)return "";
            return String.valueOf(activeObject);
        }else {
            Object objectValue = getObjectValue(objectField, object);
            if (objectValue == null) return null;
            return String.valueOf(objectValue);
        }
    }

    Object getObjectValue(String objectField, Object object) throws NoSuchFieldException, IllegalAccessException {
        Field fieldObject = object.getClass().getDeclaredField(objectField);
        fieldObject.setAccessible(true);
        Object objectValue = fieldObject.get(object);
        return objectValue;
    }

}