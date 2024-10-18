package back_end.audio_video;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
public class AudioVideoApplication {

	public static void main(String[] args) {
		SpringApplication.run(AudioVideoApplication.class, args);
	}

}
