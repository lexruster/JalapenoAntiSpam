package su.Jalapeno.AntiSpam.Services;

import com.google.inject.Inject;

/**
 * Created by Kseny on 30.12.13.
 */
public class RequestQueue {

    private JalapenoHttpService _jalapenoHttpService;

    @Inject
    public RequestQueue(JalapenoHttpService jalapenoHttpService) {
        _jalapenoHttpService = jalapenoHttpService;
    }

    public void ComplainRequest(String phone, String hash) {
        if (!_jalapenoHttpService.TryComplain(phone)) {
            AddComplainRequest(phone, hash);
        }
    }

    public void AddComplainRequest(String phone, String smsTexthash) {

    }

    public void ProceedComplainRequests() {

    }
}
