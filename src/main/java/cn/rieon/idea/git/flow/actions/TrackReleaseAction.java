package cn.rieon.idea.git.flow.actions;

import cn.rieon.idea.git.flow.GitflowConfigUtil;
import cn.rieon.idea.git.flow.ui.GitflowBranchChooseDialog;
import cn.rieon.idea.git.flow.ui.NotifyUtil;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import git4idea.commands.GitCommandResult;
import git4idea.repo.GitRepository;
import java.util.ArrayList;
import java.util.Iterator;
import org.jetbrains.annotations.NotNull;

public class TrackReleaseAction extends GitflowAction {

  public TrackReleaseAction() {
    super("Track Release");
  }

  TrackReleaseAction(GitRepository repo) {
    super(repo, "Track Release");
  }

  @Override
  public void actionPerformed(AnActionEvent e) {
    super.actionPerformed(e);

    ArrayList<String> remoteBranches = branchUtil.getRemoteBranchNames();
    ArrayList<String> remoteReleaseBranches = new ArrayList<String>();

    //get only the branches with the proper prefix
    for (Iterator<String> i = remoteBranches.iterator(); i.hasNext(); ) {
      String item = i.next();
      if (item.contains(releasePrefix)) {
        remoteReleaseBranches.add(item);
      }
    }

    if (remoteBranches.size() > 0) {
      GitflowBranchChooseDialog branchChoose = new GitflowBranchChooseDialog(myProject,
          remoteReleaseBranches);

      branchChoose.show();
      if (branchChoose.isOK()) {
        String branchName = branchChoose.getSelectedBranchName();
        final String releaseName = GitflowConfigUtil
            .getReleaseNameFromBranch(myProject, myRepo, branchName);
        final GitflowErrorsListener errorLineHandler = new GitflowErrorsListener(myProject);

        new Task.Backgroundable(myProject, "Tracking release " + releaseName, false) {
          @Override
          public void run(@NotNull ProgressIndicator indicator) {
            GitCommandResult result = myGitflow.trackRelease(myRepo, releaseName, errorLineHandler);

            if (result.success()) {
              String trackedReleaseMessage = String
                  .format(" A new remote tracking branch '%s%s' was created", releasePrefix,
                      releaseName);
              NotifyUtil.notifySuccess(myProject, releaseName, trackedReleaseMessage);
            } else {
              NotifyUtil.notifyError(myProject, "Error",
                  "Please have a look at the Version Control console for more details");
            }

            myRepo.update();
          }
        }.queue();
      }
    } else {
      NotifyUtil.notifyError(myProject, "Error", "No remote branches");
    }

  }
}