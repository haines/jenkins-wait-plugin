package nz.org.haines.jenkins.plugins.wait;

import hudson.Extension;
import hudson.model.RootAction;
import org.kohsuke.stapler.Header;
import org.kohsuke.stapler.HttpResponse;
import org.kohsuke.stapler.HttpResponses;
import org.kohsuke.stapler.QueryParameter;


@Extension
public class ResponseHandler implements RootAction {
    public HttpResponse doConfirm(@QueryParameter(required = true) String id, @Header("Referer") String referer) {
        Responses.getInstance().confirm(id);
        return HttpResponses.redirectTo(referer);
    }

    public HttpResponse doCancel(@QueryParameter(required = true) String id, @Header("Referer") String referer) {
        Responses.getInstance().cancel(id);
        return HttpResponses.redirectTo(referer);
    }

    @Override
    public String getUrlName() {
        return "wait";
    }

    @Override
    public String getIconFileName() {
        return null;
    }

    @Override
    public String getDisplayName() {
        return null;
    }
}
