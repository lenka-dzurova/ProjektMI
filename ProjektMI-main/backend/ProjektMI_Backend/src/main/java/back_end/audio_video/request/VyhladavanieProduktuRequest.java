package back_end.audio_video.request;


import back_end.audio_video.details.Rola;
import back_end.audio_video.details.Technika;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VyhladavanieProduktuRequest {
    private Rola rolaProduktu;
    private Technika typTechniky;
    private String nazov;
}
