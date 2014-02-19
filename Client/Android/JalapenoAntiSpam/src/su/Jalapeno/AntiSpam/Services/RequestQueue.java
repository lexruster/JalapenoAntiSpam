package su.Jalapeno.AntiSpam.Services;

/**
 * Created by Kseny on 30.12.13.
 */
public class RequestQueue {

    private JalapenoHttpService jalapenoHttpService;

    public RequestQueue(JalapenoHttpService jalapenoHttpService) {

        this.jalapenoHttpService = jalapenoHttpService;
    }

    public void ComplainRequest(String phone, String hash) {
        if (!jalapenoHttpService.TryComplain(phone)) {
            AddComplainRequest(phone, hash);
        }
    }

    public void AddComplainRequest(String phone, String smsTexthash) {

    }

    public void ProceedComplainRequests() {

    }
}
