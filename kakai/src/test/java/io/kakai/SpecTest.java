package io.kakai;

import io.kakai.exception.KakaiException;
import io.kakai.model.web.HttpResponse;
import io.kakai.web.ExperienceProcessor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SpecTest extends BaseTest {

    @Test
    public void a() throws KakaiException, NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        HttpResponse resp = this.create();
        ExperienceProcessor exp = new ExperienceProcessor();
        StringBuilder sb = new StringBuilder();
        sb.append("<kakai:iterate items=\"${todos}\" var=\"tdo\">\n");
        sb.append("     <kakai:if spec=\"${tdo.title == 'Exercise *0'}\">\n");
        sb.append("         *ned.\n");
        sb.append("     </kakai:if>\n");
        sb.append("     <kakai:if spec=\"${tdo.title == 'Exercise *1'}\">\n");
        sb.append("         *jermaine.\n");
        sb.append("     </kakai:if>\n");
        sb.append("</kakai:iterate>\n");
        String result = exp.execute(new HashMap<>(), sb.toString(), resp, null,null).replaceAll("([^\\S\\r\\n])+|(?:\\r?\\n)+", "");
        assertEquals("*ned.*jermaine.", result);
    }

    @Test
    public void b() throws KakaiException, NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        HttpResponse resp = this.create();
        ExperienceProcessor exp = new ExperienceProcessor();
        StringBuilder sb = new StringBuilder();
        sb.append("<kakai:if spec=\"${message != ''}\">\n");
        sb.append("${message}\n");
        sb.append("</kakai:if>\n");
        String result = exp.execute(new HashMap<>(), sb.toString(), resp, null,null).trim();
        assertEquals("Strain.", result);
    }

    @Test
    public void c() throws KakaiException, NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        HttpResponse resp = this.create();
        ExperienceProcessor exp = new ExperienceProcessor();
        StringBuilder sb = new StringBuilder();
        sb.append("<kakai:if spec=\"${blank != ''}\">\n");
        sb.append("Nothing.\n");
        sb.append("</kakai:if>\n");
        String result = exp.execute(new HashMap<>(), sb.toString(), resp, null,null).trim();
        assertEquals("", result);
    }

    @Test
    public void d() throws KakaiException, NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        HttpResponse resp = this.create();
        ExperienceProcessor exp = new ExperienceProcessor();
        StringBuilder sb = new StringBuilder();
        sb.append("<kakai:if spec=\"${tom.name == 'Tom'}\">\n");
        sb.append("Tom.\n");
        sb.append("</kakai:if>\n");
        sb.append("<kakai:if spec=\"${tom.wife.name == 'Penelope'}\">\n");
        sb.append("Penelope.\n");
        sb.append("</kakai:if>\n");
        sb.append("<kakai:if spec=\"${tom.wife.pet.name == 'Diego'}\">\n");
        sb.append("Diego.\n");
        sb.append("</kakai:if>\n");
        String result = exp.execute(new HashMap<>(), sb.toString(), resp, null,null).replaceAll("([^\\S\\r\\n])+|(?:\\r?\\n)+", "");
        assertEquals("Tom.Penelope.Diego.", result);
    }

    @Test
    public void e() throws KakaiException, NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        HttpResponse resp = this.create();
        ExperienceProcessor exp = new ExperienceProcessor();
        StringBuilder sb = new StringBuilder();
        sb.append("<kakai:if spec=\"${not == null}\">\n");
        sb.append("not.\n");
        sb.append("</kakai:if>\n");
        sb.append("<kakai:if spec=\"${not != null}\">\n");
        sb.append("!not.\n");
        sb.append("</kakai:if>\n");
        String result = exp.execute(new HashMap<>(), sb.toString(), resp, null,null).replaceAll("([^\\S\\r\\n])+|(?:\\r?\\n)+", "");
        assertEquals("not.", result);
    }

    @Test
    public void f() throws KakaiException, NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        HttpResponse resp = this.create();
        StringBuilder sb = new StringBuilder();
        ExperienceProcessor exp = new ExperienceProcessor();
        sb.append("<kakai:if spec=\"${todos.size() > 0}\">\n");
        sb.append(" <kakai:iterate items=\"${todos}\" var=\"tdo\">\n");
        sb.append("     <kakai:set var=\"selected\" val=\"\" \n");
        sb.append("     <kakai:if spec=\"${tdo.title == 'Exercise *1'}\">\n");
        sb.append("         <kakai:set var=\"selected\" val=\"selected\"\n");
        sb.append("     </kakai:if>\n");
        sb.append("     :${selected}:\n");
        sb.append("     <kakai:iterate items=\"${tdo.people}\" var=\"person\">\n");
        sb.append("         ${tdo.id} -> ${person.pet.name},\n");
        sb.append("     </kakai:iterate>\n");
        sb.append(" </kakai:iterate>\n");
        sb.append("</kakai:if>\n");
        sb.append("<kakai:if spec=\"${todos.size() == 0}\">\n");
        sb.append("     Nothing.\n");
        sb.append("</kakai:if>\n");
        String result = exp.execute(new HashMap<>(), sb.toString(), resp, null,null).replaceAll("([^\\S\\r\\n])+|(?:\\r?\\n)+", "");
        assertEquals("::0->Apache*0,0->Apache*1,0->Apache*2,:selected:1->Apache*0,1->Apache*1,1->Apache*2,::2->Apache*0,2->Apache*1,2->Apache*2,", result);
    }

}
