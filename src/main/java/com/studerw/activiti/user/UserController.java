package com.studerw.activiti.user;

import com.studerw.activiti.model.Response;
import com.studerw.activiti.model.UserForm;
import com.studerw.activiti.web.BaseController;
import org.activiti.engine.identity.Group;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class UserController extends BaseController {
    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

    @Autowired UserService userService;

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    @RequestMapping(value = "/users", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response<List<UserForm>>> getUsers(HttpServletRequest request) {
        List<UserForm> users = userService.getAllUsers();
        LOG.trace("returning json response of {} users",  users.size());
        Response res = new Response(true, "users",  users);
        return new ResponseEntity<Response<List<UserForm>>>(res, HttpStatus.OK);
    }

    @RequestMapping(value = "/groups", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response<List<Group>>> getGroups(HttpServletRequest request) {
        List<Group> groups = userService.getAllAssignmentGroups();
        LOG.trace("returning json response of {} groups",  groups.size());
        Response res = new Response(true, "groups",  groups);
        return new ResponseEntity<Response<List<Group>>>(res, HttpStatus.OK);
    }

    @RequestMapping(value = "/users.htm", method = RequestMethod.GET)
    public String getDocuments(ModelMap model, HttpServletRequest request) {
        return "users";
    }


}
