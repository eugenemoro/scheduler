package ru.gvg.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import ru.geekunivercity.entity.task.Task;
import ru.geekunivercity.entity.task.TaskStatus;
import ru.geekunivercity.entity.user.AppUser;
import ru.geekunivercity.service.task.TaskServiceImpl;
import ru.geekunivercity.service.appuser.AppUserServiceImpl;

import java.util.Date;
import java.util.Map;

/**
 * Контроллер для работы с задачами.
 *
 * @author Valeriy Gyrievskikh
 * @since 05.09.2018.
 */

@Controller
@RequestMapping("/task")
public class TaskController {
    /**
     * Сервис для работы с пользователями.
     */
    @Autowired
    AppUserServiceImpl userService;

    /**
     * Сервис для работы с задачами.
     */
    @Autowired
    TaskServiceImpl taskService;

    /**
     * Метод возвращает список задач с отбором по пользователю и текущей дате.
     *
     * @param userEmail Почта пользователя (для идентификации пользователя).
     * @param model     Список задач.
     * @return Страница списка задач.
     */
    @RequestMapping(value = {"/task-list"}, method = RequestMethod.GET)
    public String tasksList(@RequestParam("email") String userEmail, Map<String, Object> model) {
        AppUser user = userService.findByEmail(userEmail);
        if (user == null) {
            return "login";
        }
        model.put("taskList", taskService.getTaskSetByActualStartTimeAndAppUserId(new Date(), user.getId()));
        return "task-list";
    }

    /**
     * Метод создает новую задачу с привязкой к текущему пользователю.
     *
     * @param userEmail Почта пользователя (для идентификации пользователя).
     * @param model     Созданная задача.
     * @return Страница редактирования задачи.
     */
    @RequestMapping(value = {"/task-create"}, method = RequestMethod.GET)
    public String taskCreate(@RequestParam("email") String userEmail, Map<String, Object> model) {
        Task task = new Task();
        AppUser user = userService.findByEmail(userEmail);
        if (user == null) {
            return "redirect:/task/task-list";
        }
        task.setTaskStatus(TaskStatus.AWAITS_EXECUTION);
        model.put("task", task);
        return "task-edit";
    }

    /**
     * Метод вызывает страницу редактирования выбранной задачи.
     *
     * @param taskId Идентификатор задачи.
     * @param model  Выборанная задача.
     * @return Страница редактирования задачи.
     */
    @RequestMapping(value = {"/edit"}, method = RequestMethod.POST)
    public String taskEdit(@RequestParam("taskId") String taskId, Map<String, Object> model) {
        final Task task = taskService.findTaskById(taskId);
        model.put("task", task);
        return "task-edit";
    }

    /**
     * Метод сохраняет изменения задачи в открывает страницу списка задач.
     *
     * @param task Редактируемая задача.
     * @return Страница списка задач.
     */
    @RequestMapping(value = {"/task-save"}, method = RequestMethod.POST)
    public String taskSave(@ModelAttribute("task") Task task) {
        taskService.mergeTask(task);
        return "redirect:/task/task-list";
    }

    /**
     * Метод отменяет редактирование задачи.
     *
     * @return Страница списка задач.
     */
    @RequestMapping(value = {"/task-cancel"}, method = RequestMethod.POST)
    public String taskCancelSave() {
        return "redirect:/task/task-list";
    }

    /**
     * Метод удаляет выбранную задачу.
     *
     * @param taskId Идентификатор выбранной задачи.
     * @return Страница списка задач.
     */
    @RequestMapping(value = {"/delete"}, method = RequestMethod.POST)
    public String taskDelete(@RequestParam("taskId") String taskId) {
        taskService.deleteTaskById(taskId);
        return "redirect:/task/task-list";
    }

}
