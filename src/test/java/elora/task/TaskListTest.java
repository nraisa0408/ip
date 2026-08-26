package elora.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;

class TaskListTest {

    @Test
    void size_newTaskList_isZero() {
        TaskList tasks = new TaskList();
        assertEquals(0, tasks.size());
    }

    @Test
    void add_singleTask_increasesSizeByOne() {
        TaskList tasks = new TaskList();
        tasks.add(new Task("read book"));
        assertEquals(1, tasks.size());
    }

    @Test
    void get_validIndex_returnsTaskAtThatPosition() {
        TaskList tasks = new TaskList();
        Task first = new Task("read book");
        Task second = new Task("return book");
        tasks.add(first);
        tasks.add(second);
        assertEquals(first, tasks.get(0));
        assertEquals(second, tasks.get(1));
    }

    @Test
    void remove_validIndex_returnsRemovedTaskAndShrinksList() {
        TaskList tasks = new TaskList();
        Task first = new Task("read book");
        Task second = new Task("return book");
        tasks.add(first);
        tasks.add(second);

        Task removed = tasks.remove(0);

        assertEquals(first, removed);
        assertEquals(1, tasks.size());
        assertEquals(second, tasks.get(0));
    }

    @Test
    void getTasksOnDate_deadlineOnThatDate_isIncluded() {
        TaskList tasks = new TaskList();
        Deadline dueOnTarget = new Deadline("pay bills", LocalDate.parse("2019-10-15"));
        tasks.add(dueOnTarget);

        ArrayList<Task> matches = tasks.getTasksOnDate(LocalDate.parse("2019-10-15"));

        assertEquals(1, matches.size());
        assertTrue(matches.contains(dueOnTarget));
    }

    @Test
    void getTasksOnDate_noTaskOnThatDate_returnsEmptyList() {
        TaskList tasks = new TaskList();
        tasks.add(new Deadline("pay bills", LocalDate.parse("2019-10-15")));

        ArrayList<Task> matches = tasks.getTasksOnDate(LocalDate.parse("2099-01-01"));

        assertEquals(0, matches.size());
    }

    @Test
    void getTasksOnDate_todoOnAnyDate_isNeverIncluded() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("unrelated"));

        ArrayList<Task> matches = tasks.getTasksOnDate(LocalDate.parse("2019-10-15"));

        assertEquals(0, matches.size());
    }
}
