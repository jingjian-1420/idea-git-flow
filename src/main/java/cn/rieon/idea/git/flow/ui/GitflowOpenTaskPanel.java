package cn.rieon.idea.git.flow.ui;

import static com.intellij.openapi.vcs.VcsTaskHandler.TaskInfo;

import cn.rieon.idea.git.flow.GitflowBranchUtil;
import cn.rieon.idea.git.flow.GitflowBranchUtilManager;
import cn.rieon.idea.git.flow.GitflowConfigUtil;
import cn.rieon.idea.git.flow.GitflowState;
import cn.rieon.idea.git.flow.actions.GitflowAction;
import cn.rieon.idea.git.flow.actions.StartFeatureAction;
import cn.rieon.idea.git.flow.actions.StartHotfixAction;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.VcsTaskHandler;
import com.intellij.tasks.LocalTask;
import com.intellij.tasks.Task;
import com.intellij.tasks.TaskManager;
import com.intellij.tasks.impl.TaskManagerImpl;
import com.intellij.tasks.ui.TaskDialogPanel;
import git4idea.branch.GitBranchUtil;
import git4idea.repo.GitRepository;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Collections;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import org.jetbrains.annotations.NotNull;

public class GitflowOpenTaskPanel extends TaskDialogPanel implements ItemListener {

  private JRadioButton noActionRadioButton;
  private JRadioButton startFeatureRadioButton;
  private JRadioButton startHotfixRadioButton;
  private JTextField featureName;
  private JComboBox featureBaseBranch;
  private JTextField hotfixName;
  private JPanel myPanel;
  private JComboBox hotfixBaseBranch;

  private Project myProject;
  private GitRepository myRepo;
  private GitflowBranchUtil gitflowBranchUtil;
  private TaskManagerImpl myTaskManager;
  private VcsTaskHandler myVcsTaskHandler;
  private LocalTask myPreviousTask;
  private Task currentTask;

  private GitflowState gitflowState;


  public GitflowOpenTaskPanel(Project project, Task task, GitRepository repo) {
    myProject = project;
    currentTask = task;
    myRepo = repo;

    myTaskManager = (TaskManagerImpl) TaskManager.getManager(project);
    myPreviousTask = myTaskManager.getActiveTask();
    VcsTaskHandler[] vcsTaskHAndlers = VcsTaskHandler.getAllHandlers(project);
    if (vcsTaskHAndlers.length > 0) {
      //todo handle case of multiple vcs handlers
      myVcsTaskHandler = vcsTaskHAndlers[0];
    }

    gitflowState = ServiceManager.getService(GitflowState.class);
    gitflowBranchUtil = GitflowBranchUtilManager.getBranchUtil(myRepo);

    String defaultFeatureBranch = GitflowConfigUtil.getDevelopBranch(project, myRepo);
    featureBaseBranch.setModel(gitflowBranchUtil.createBranchComboModel(defaultFeatureBranch));

    String defaultHotfixBranch = GitflowConfigUtil.getMasterBranch(project, myRepo);
    hotfixBaseBranch.setModel(gitflowBranchUtil.createBranchComboModel(defaultHotfixBranch));

    myRepo = GitBranchUtil.getCurrentRepository(project);

    String branchName = myVcsTaskHandler != null
        ? myVcsTaskHandler.cleanUpBranchName(myTaskManager.constructDefaultBranchName(task))
        : myTaskManager.suggestBranchName(task);

    featureName.setText(branchName);
    featureName.setEditable(false);
    featureName.setEnabled(false);

    hotfixName.setText(branchName);
    hotfixName.setEditable(false);
    hotfixName.setEnabled(false);

    featureBaseBranch.setEnabled(false);
    hotfixBaseBranch.setEnabled(false);

    //add listeners
    noActionRadioButton.addItemListener(this);
    startFeatureRadioButton.addItemListener(this);
    startHotfixRadioButton.addItemListener(this);

  }

  @NotNull
  @Override
  public JComponent getPanel() {
    return myPanel;
  }

  @Override
  public void commit() {

    final GitflowBranchUtil.ComboEntry selectedFeatureBaseBranch = (GitflowBranchUtil.ComboEntry) featureBaseBranch
        .getModel().getSelectedItem();
    final GitflowBranchUtil.ComboEntry selectedHotfixBaseBranch = (GitflowBranchUtil.ComboEntry) hotfixBaseBranch
        .getModel().getSelectedItem();

    if (startFeatureRadioButton.isSelected()) {
      final String branchName =
          GitflowConfigUtil.getFeaturePrefix(myProject, myRepo) + featureName.getText();
      attachTaskAndRunAction(new StartFeatureAction(myRepo),
          selectedFeatureBaseBranch.getBranchName(), branchName);
    } else if (startHotfixRadioButton.isSelected()) {
      final String branchName =
          GitflowConfigUtil.getHotfixPrefix(myProject, myRepo) + hotfixName.getText();
      attachTaskAndRunAction(new StartHotfixAction(myRepo),
          selectedHotfixBaseBranch.getBranchName(), branchName);
    }
  }

  /**
   * @param action instance of GitflowAction
   * @param baseBranchName Branch name of the branch the new one is based on
   * @param fullBranchName Branch name with feature/hotfix prefix for saving to task
   */
  private void attachTaskAndRunAction(GitflowAction action, String baseBranchName,
      final String fullBranchName) {
    final String branchName = gitflowBranchUtil.stripFullBranchName(fullBranchName);

    //Create new branch / checkout branch
    action.runAction(myProject, baseBranchName, branchName, new Runnable() {
      @Override
      public void run() {
        final TaskInfo[] next = {
            new TaskInfo(fullBranchName, Collections.singleton(myRepo.getPresentableUrl()))};
        final LocalTask localTask = myTaskManager.getActiveTask();

        //Add branch to task
        TaskManagerImpl.addBranches(localTask, next, false);

        //maps branch to task
        gitflowState.setTaskBranch(currentTask, fullBranchName);
      }
    });
  }

  @Override
  public void itemStateChanged(ItemEvent e) {
    Object source = e.getItemSelectable();

    //disable\enable textfields based on radio selection
    if (e.getStateChange() == ItemEvent.SELECTED && source == noActionRadioButton) {
      featureName.setEditable(false);
      featureName.setEnabled(false);
      featureBaseBranch.setEnabled(false);

      hotfixName.setEditable(false);
      hotfixName.setEnabled(false);
      hotfixBaseBranch.setEnabled(false);
    } else if (e.getStateChange() == ItemEvent.SELECTED && source == startFeatureRadioButton) {
      featureName.setEditable(true);
      featureName.setEnabled(true);
      featureBaseBranch.setEnabled(true);

      hotfixName.setEditable(false);
      hotfixName.setEnabled(false);
      hotfixBaseBranch.setEnabled(false);
    } else if (e.getStateChange() == ItemEvent.SELECTED && source == startHotfixRadioButton) {
      featureName.setEditable(false);
      featureName.setEnabled(false);
      featureBaseBranch.setEnabled(false);

      hotfixName.setEditable(true);
      hotfixName.setEnabled(true);
      hotfixBaseBranch.setEnabled(true);
    }

  }
}
