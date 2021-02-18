//package com.studerw.activiti;
//
//import com.google.common.base.Joiner;
//import com.google.common.collect.Lists;
//import com.studerw.activiti.document.DocumentService;
//import com.studerw.activiti.model.Document;
//import com.studerw.activiti.model.TaskForm;
//import com.studerw.activiti.task.LocalTaskService;
//import com.studerw.activiti.user.UserService;
//import org.activiti.engine.HistoryService;
//import org.activiti.engine.IdentityService;
//import org.activiti.engine.RuntimeService;
//import org.activiti.engine.TaskService;
//import org.activiti.engine.history.HistoricProcessInstance;
//import org.activiti.engine.history.HistoricTaskInstance;
//import org.activiti.engine.identity.Group;
//import org.activiti.engine.task.Comment;
//
//
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.AuthorityUtils;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//
//import java.util.Collection;
//import java.util.Date;
//import java.util.List;
//import java.util.Map;
//
//import static org.junit.Assert.assertNotNull;
//import static org.junit.Assert.assertTrue;
//
///**
// * @author William Studer
// * Date: 5/21/14
// */
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration({"classpath:spring/testAppContext.xml"})
//public class DocumentApproveTest {
//    private static final Logger LOG = LoggerFactory.getLogger(ActivitiSpringTest.class);
//    @Autowired
//    RuntimeService runtimeService;
//    @Autowired
//    TaskService taskService;
//    @Autowired
//    HistoryService historyService;
//    @Autowired
//    IdentityService identityService;
//    @Autowired
//    UserService userService;
//    @Autowired
//    DocumentService documentService;
//    @Autowired
//    LocalTaskService localTaskService;
//
//    @Test
//    public void testDocApprovalFlow() throws InterruptedException {
//        setSpringSecurity("kermit");
//        Document doc = new Document();
//        doc.setGroupId("engineering");
//        doc.setCreatedDate(new Date());
//        doc.setTitle("title");
//        doc.setSummary("Summary");
//        doc.setContent("content");
//        doc.setAuthor("kermit");
//        String docId;
//        docId = documentService.createDocument(doc);
//        LOG.debug("new doc id: " + docId);
//        this.documentService.submitToWorkflow(docId);
//
//        setSpringSecurity("fozzie");
//        List<TaskForm> tasks = this.localTaskService.findCandidateTasks("fozzie");
//        assertTrue(tasks.size() == 1);
//        LOG.debug("got task: " + tasks.get(0).getName());
//        localTaskService.approveOrRejectDoc(true, "task approved blah blah blah", tasks.get(0).getId());
//        HistoricProcessInstance pi = historyService.createHistoricProcessInstanceQuery().
//                includeProcessVariables().processInstanceBusinessKey(docId).singleResult();
//
//        assertNotNull(pi);
//        LOG.debug("Duration time in millis: " + pi.getDurationInMillis());
//        List<HistoricTaskInstance> hTasks;
//        hTasks = historyService.createHistoricTaskInstanceQuery().includeTaskLocalVariables().processInstanceBusinessKey(docId).list();
//        assertTrue(hTasks.size() == 2);
//        HistoricTaskInstance approval = hTasks.get(1);
//        Map<String, Object> vars = approval.getTaskLocalVariables();
//        List<Comment> comments = taskService.getTaskComments(approval.getId());
//    }
//
//    private void setSpringSecurity(String userName) {
//        List<Group> groups = this.identityService.createGroupQuery().groupMember(userName).groupType("security-role").list();
//        List<String> groupStr = Lists.newArrayList();
//        for (Group g : groups) {
//            groupStr.add(g.getId());
//        }
//        Collection<GrantedAuthority> auths = AuthorityUtils.commaSeparatedStringToAuthorityList(Joiner.on(",").skipNulls().join(groupStr));
//        UserDetails userDetails = new org.springframework.security.core.userdetails.User(userName, "pw", true, true, true, true, auths);
//        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//        SecurityContextHolder.getContext().setAuthentication(auth);
//    }
//}
