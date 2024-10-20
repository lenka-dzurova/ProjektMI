package back_end.audio_video.component;

import back_end.audio_video.service.DeleteExirationTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks {

    @Autowired
    private DeleteExirationTokenService deleteExirationTokenService;

    @Scheduled(fixedRate = 86400000)
    public void removeExpiredUsersAndTokens() {
        deleteExirationTokenService.removeAllExpiredUsersAndTokens();
    }
}

