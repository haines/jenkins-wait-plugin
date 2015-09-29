package nz.org.haines.jenkins.plugins.wait;

import hudson.AbortException;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.FormValidation;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.UUID;

import static org.apache.commons.lang.StringUtils.defaultIfBlank;


public class PromptBuilder extends Builder {
    private final String prompt;
    private final String confirm;
    private final String cancel;

    @DataBoundConstructor
    public PromptBuilder(String prompt, String confirm, String cancel) {
        this.prompt = prompt;
        this.confirm = defaultIfBlank(confirm, "OK");
        this.cancel = defaultIfBlank(cancel, "Cancel");
    }

    @Override
    public boolean perform(final AbstractBuild<?, ?> build, final Launcher launcher, final BuildListener listener) throws InterruptedException, IOException {
        String id = UUID.randomUUID().toString();

        printPrompt(listener, id);
        waitForResponse(id);
        handleResponse(listener, id);

        return true;
    }

    private void printPrompt(BuildListener listener, String id) throws IOException {
        listener.getLogger().println();
        listener.getLogger().println("*************************************************");
        listener.getLogger().println();
        listener.getLogger().println(prompt);
        listener.getLogger().println();
        listener.hyperlink("/wait/confirm?id=" + id, confirm);
        listener.getLogger().print("         ");
        listener.hyperlink("/wait/cancel?id=" + id, cancel);
        listener.getLogger().println();
        listener.getLogger().println();
        listener.getLogger().println("*************************************************");
        listener.getLogger().println();
    }

    private void waitForResponse(String id) throws InterruptedException {
        while (getResponse(id) == null) {
            Thread.sleep(100);
        }
    }

    private void handleResponse(BuildListener listener, String id) throws AbortException {
        if (getResponse(id)) {
            listener.getLogger().println("Confirmed");
            listener.getLogger().println();
        } else {
            throw new AbortException("Cancelled");
        }
    }

    private Boolean getResponse(String id) {
        return Responses.getInstance().get(id);
    }

    public String getPrompt() {
        return prompt;
    }

    public String getConfirm() {
        return confirm;
    }

    public String getCancel() {
        return cancel;
    }


    @Extension
    public static class PromptDescriptor extends BuildStepDescriptor<Builder> {
        @Override
        public boolean isApplicable(final Class<? extends AbstractProject> projectClass) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "Wait for human intervention";
        }

        public FormValidation doCheckPrompt(@QueryParameter String value) throws IOException, ServletException {
            return validateNotEmpty(value, "Please set a prompt");
        }

        private FormValidation validateNotEmpty(@QueryParameter String value, String message) {
            if (value.isEmpty()) return FormValidation.error(message);

            return FormValidation.ok();
        }
    }
}
