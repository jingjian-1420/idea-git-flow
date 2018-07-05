package cn.rieon.idea.git.flow.ui;

import cn.rieon.idea.git.flow.GitflowBranchUtil;
import cn.rieon.idea.git.flow.GitflowBranchUtilManager;
import cn.rieon.idea.git.flow.GitflowConfigurable;
import cn.rieon.idea.git.flow.GitflowState;
import cn.rieon.idea.git.flow.actions.FinishFeatureAction;
import cn.rieon.idea.git.flow.actions.FinishHotfixAction;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.VcsTaskHandler;
import com.intellij.tasks.Task;
import com.intellij.tasks.TaskManager;
import com.intellij.tasks.impl.TaskManagerImpl;
import com.intellij.tasks.ui.TaskDialogPanel;
import git4idea.repo.GitRepository;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.jetbrains.annotations.NotNull;

public class GitflowCloseTaskPanel extends TaskDialogPanel {

  final private GitflowState gitflowState;
  private JPanel myPanel;
  private JCheckBox finishFeatureCheckbox;
  private JCheckBox finishHotfixCheckbox;
  private JPanel finishHotfixPanel;
  private JTextField tagMessageTextField;
  private JPanel finishFeaturePanel;
  private Project myProject;
  private Task myTask;
  private GitRepository myRepo;
  private GitflowBranchUtil gitflowBranchUtil;
  private TaskManagerImpl myTaskManager;
  private VcsTaskHandler myVcsTaskHandler;
  private String tagMessage;

  GitflowCloseTaskPanel(Project project, Task task, GitRepository repo) {
    myProject = project;
    myTask = task;
    myRepo = repo;
    gitflowState = ServiceManager.getService(GitflowState.class);

    gitflowBranchUtil = GitflowBranchUtilManager.getBranchUtil(myRepo);
    myTaskManager = (TaskManagerImpl) TaskManager.getManager(project);
    VcsTaskHandler[] vcsTaskHAndlers = VcsTaskHandler.getAllHandlers(project);
    if (vcsTaskHAndlers.length > 0) {
      //todo handle case of multiple vcs handlers
      myVcsTaskHandler = vcsTaskHAndlers[0];
    }

    String branchName = myVcsTaskHandler != null
        ? myVcsTaskHandler.cleanUpBranchName(myTaskManager.constructDefaultBranchName(task))
        : myTaskManager.suggestBranchName(task);

    //TODO same code exists in FinishHotfixAction, make DRYer

    if (GitflowConfigurable.isOptionActive(project, "HOTFIX_dontTag")) {
      tagMessage = "";
      tagMessageTextField.setEnabled(false);
      tagMessageTextField.setToolTipText("Hotfix tagging is disabled in Gitflow options");

    } else {
      tagMessage = GitflowConfigurable
          .getOptionTextString(project, "HOTFIX_customHotfixCommitMessage")
          .replace("%name%", branchName);
      tagMessageTextField.setToolTipText(null);
    }

    tagMessageTextField.setText(tagMessage);

  }

  @NotNull
  @Override
  public JComponent getPanel() {
    String taskBranchName = gitflowState.getTaskBranch(myTask);
    if (taskBranchName != null) {

      myPanel.setVisible(true);
      if (gitflowBranchUtil.isBranchFeature(taskBranchName)) {
        finishFeaturePanel.setVisible(true);
        finishHotfixPanel.setVisible(false);
      } else if (gitflowBranchUtil.isBranchHotfix(taskBranchName)) {
        finishFeaturePanel.setVisible(false);
        finishHotfixPanel.setVisible(true);
      }

    } else {
      myPanel.setVisible(false);
    }

    return myPanel;
  }

  @Override
  public void commit() {
    String taskFullBranchName = gitflowState.getTaskBranch(myTask);

    // test if current task is a gitflow task
    if (taskFullBranchName != null) {
      String taskBranchName = gitflowBranchUtil.stripFullBranchName(taskFullBranchName);

      if (finishFeatureCheckbox.isSelected()) {
        FinishFeatureAction action = new FinishFeatureAction(myRepo);
        action.runAction(myProject, taskBranchName);
      } else if (finishHotfixCheckbox.isSelected()) {
        FinishHotfixAction action = new FinishHotfixAction(myRepo);
        action.runAction(myProject, taskBranchName, tagMessageTextField.getText());
      }
    }
  }
}
