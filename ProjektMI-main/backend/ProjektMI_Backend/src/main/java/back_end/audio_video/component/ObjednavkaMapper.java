package back_end.audio_video.component;


import back_end.audio_video.dto.ObjednavkaDTO;
import back_end.audio_video.dto.ObjednavkaProduktDTO;
import back_end.audio_video.entity.Objednavka;
import back_end.audio_video.entity.ObjednavkaProdukt;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ObjednavkaMapper {


    public ObjednavkaDTO objednavkaToDTO(Objednavka objednavka) {
        ObjednavkaDTO dto = new ObjednavkaDTO();
        dto.setIdObjednavka(objednavka.getIdObjednavka());
        dto.setDatumVypozicania(objednavka.getDatumVypozicania());
        dto.setDatumVratenia(objednavka.getDatumVratenia());
        dto.setIdPouzivatela(objednavka.getPouzivatel().getIdPouzivatel());

        List<ObjednavkaProduktDTO> produktyDTO = objednavka.getObjednavkaProdukty()
                        .stream()
                        .map(this::objednavkaProduktToDTO).toList();

        dto.setObjednavkaProdukty(produktyDTO);
        return dto;
    }

    private ObjednavkaProduktDTO objednavkaProduktToDTO(ObjednavkaProdukt objednavkaProdukt) {
        ObjednavkaProduktDTO dto = new ObjednavkaProduktDTO();
        dto.setId(objednavkaProdukt.getId());
        dto.setProduktId(objednavkaProdukt.getProdukt().getIdProdukt());

        return dto;
    }
}
