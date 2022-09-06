package io.kakai;

import io.kakai.extras.Actor;
import io.kakai.extras.Person;
import io.kakai.extras.Pet;
import io.kakai.extras.Todo;
import io.kakai.model.web.HttpResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;

import java.util.ArrayList;
import java.util.List;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class BaseTest {

    Kakai kakai;
    Boolean dispatchedEvent;

    @BeforeEach
    public void setup() throws Exception {
        kakai = new Kakai(8080)
                        .createDevelopmentDatabase()
                        .start();
    }

    @AfterEach
    public void shutdown(){
        kakai.stop();
    }

    public HttpResponse create() {

        HttpResponse resp = new HttpResponse();

        List<Todo> todos = new ArrayList<>();

        Integer personIdx = 4;
        for(int idx = 0; idx < 3; idx++) {
            Todo todo = new Todo();
            todo.setId(idx);
            todo.setTitle("Exercise *" + idx);

            List<Person> people = new ArrayList<>();
            for (int idxn = 0; idxn < 3; idxn++) {
                Person person = new Person();
                person.setId(personIdx);
                person.setName("Brad *" + personIdx);personIdx++;
                Pet pet = new Pet();
                pet.setId(idxn);
                pet.setName("Apache *" + idxn);
                person.setPet(pet);
                people.add(person);
            }
            todo.setPeople(people);
            todos.add(todo);
        }

        Actor topgun = getActor();
        Actor blackjoe = getIt();
        Actor burgandy = getBlankPet();
        Actor iceman = getNilPet();

        resp.set("not", null);
        resp.set("blank", "");
        resp.set("message", "Strain.");
        resp.set("nil", "");
        resp.set("tom", topgun);
        resp.set("brad", blackjoe);
        resp.set("will", burgandy);
        resp.set("val", iceman);
        resp.set("todos", todos);
        resp.set("true", true);
        resp.set("condition", true);

        return resp;
    }

    Actor getIt(){
        Pet pet = new Pet();
        pet.setId(12);
        pet.setName("Agatha");

        Actor wife = new Actor();
        wife.setId(9);
        wife.setName("Jennifer");
        wife.setPet(pet);

        Actor actor = new Actor();
        actor.setId(10);
        actor.setName("Pitt");
        actor.setWife(wife);

        return actor;
    }

    Actor getActor(){
        Pet pet = new Pet();
        pet.setId(3);
        pet.setName("Diego");

        Actor wife = new Actor();
        wife.setId(2);
        wife.setName("Penelope");
        wife.setPet(pet);

        Actor actor = new Actor();
        actor.setId(1);
        actor.setName("Tom");
        actor.setWife(wife);

        return actor;
    }

    Actor getBlankPet(){
        Pet pet = new Pet();
        pet.setId(4);
        pet.setName("");

        Actor wife = new Actor();
        wife.setId(5);
        wife.setName("Viveca");
        wife.setPet(pet);

        Actor actor = new Actor();
        actor.setId(6);
        actor.setName("Mr. Will");
        actor.setWife(wife);

        return actor;
    }

    Actor getNilPet(){

        Actor wife = new Actor();
        wife.setId(7);
        wife.setName("Joanne");

        Actor actor = new Actor();
        actor.setId(8);
        actor.setName("Val");
        actor.setWife(wife);

        return actor;
    }

}