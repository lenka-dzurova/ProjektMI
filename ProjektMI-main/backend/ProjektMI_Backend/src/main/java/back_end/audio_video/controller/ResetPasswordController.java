package back_end.audio_video.controller;


import back_end.audio_video.entity.PasswordResetToken;
import back_end.audio_video.entity.Pouzivatel;
import back_end.audio_video.repository.PasswordResetRepository;
import back_end.audio_video.repository.PouzivatelRepository;
import back_end.audio_video.request.PasswordResetRequest;
import back_end.audio_video.service.PasswordResetService;
import back_end.audio_video.service.PouzivatelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.Locale;

@Controller
@RequestMapping("/reset-password")
public class ResetPasswordController {
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private PouzivatelService pouzivatelService;
    @Autowired
    private PasswordResetRepository passwordResetRepository;
    @Autowired
    private PouzivatelRepository pouzivatelRepository;


    @GetMapping
    public String viewPage(@RequestParam(name = "token", required = false) String token,
                           Model model){
        PasswordResetToken passwordResetToken = passwordResetRepository.findByToken(token);
        if(passwordResetToken == null){
            model.addAttribute("error", messageSource.getMessage("TOKEN_NOT_FOUND", new Object[]{}, Locale.ENGLISH));
        }else if(passwordResetToken.getExpirationDate().isBefore(LocalDateTime.now())){
            model.addAttribute("error", messageSource.getMessage("TOKEN_EXPIRED", new Object[]{}, Locale.ENGLISH));
        }else{
            model.addAttribute("token", passwordResetToken.getToken());
        }
        return "reset-password-form";
    }

    @PostMapping
    public String resetPassword(@Validated @ModelAttribute("passwordReset") PasswordResetRequest request, BindingResult result, RedirectAttributes attributes){
        if(result.hasErrors()){
            attributes.addFlashAttribute("passwordReset", request);
            return "redirect:/reset-password?token=" + request.getToken();
        }
        PasswordResetToken token = passwordResetRepository.findByToken(request.getToken());
        Pouzivatel pouzivatel = token.getPouzivatel();
        pouzivatel.setHeslo(request.getNewPassword());
        pouzivatelRepository.save(pouzivatel);
        return "redirect:/login";
    }
}
