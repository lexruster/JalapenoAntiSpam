package su.Jalapeno.AntiSpam.Services;

/**
 * Created by Kseny on 30.12.13.
 */
public class RequestQueue {

    private JalapenoHttpService jalapenoHttpService;

    public RequestQueue(JalapenoHttpService jalapenoHttpService) {

        this.jalapenoHttpService = jalapenoHttpService;
    }

    public void ComplainRequest(String phone) {
        if (!jalapenoHttpService.TryComplain(phone)) {
            AddComplainRequest(phone);
        }
    }

    private void AddComplainRequest(String phone) {

    }

    public void ProceedComplainRequests() {

    }
}
