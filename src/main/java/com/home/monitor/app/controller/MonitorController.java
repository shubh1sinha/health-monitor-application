package com.home.monitor.app.controller;

import com.home.monitor.app.model.HealthEntry;
import com.home.monitor.app.model.User;
import com.home.monitor.app.repo.HealthMonitorRepository;
import com.home.monitor.app.repo.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.Optional;

@Controller
public class MonitorController {

    private static final String AUTH_ATTR = "authenticated";

    private final UserRepository userRepo;
    private final HealthMonitorRepository entryRepo;

    public MonitorController(UserRepository userRepo, HealthMonitorRepository entryRepo) {
        this.userRepo = userRepo;
        this.entryRepo = entryRepo;
    }
    @GetMapping("/users/add")
    public String addUserForm(Model model) {
        model.addAttribute("user", new User());
        return "add-user";
    }

    @PostMapping("/users/add")
    public String addUserSubmit(@RequestParam String name,
                                @RequestParam(required = false) Integer age,
                                @RequestParam(required = false) String gender) {

        User u = new User();
        u.setName(name);
        u.setAge(age);
        u.setGender(gender);
        User saved = userRepo.save(u);
        return "redirect:/main?userId=" + saved.getId();
    }

    @GetMapping("/records/add")
    public String addRecordForm(@RequestParam Long userId, Model model) {
        Optional<User> opt = userRepo.findById(userId);
        if (opt.isEmpty()) {
            return "redirect:/main";
        }
        model.addAttribute("user", opt.get());
        model.addAttribute("entryDate", LocalDate.now());
        return "add-record";
    }

    @PostMapping("/records/add")
    public String addRecordSubmit(@RequestParam Long userId,
                                  @RequestParam(required = false) String date,
                                  @RequestParam(required = false) Integer systolic,
                                  @RequestParam(required = false) Integer diastolic,
                                  @RequestParam(required = false) Double sugarLevelFasting,
                                  @RequestParam(required = false) Double sugarLevelPostMeal) {

        Optional<User> opt = userRepo.findById(userId);
        if (opt.isEmpty()) {
            return "redirect:/main";
        }

        HealthEntry entry = new HealthEntry();
        entry.setUser(opt.get());
        entry.setDate(date == null || date.isBlank() ? LocalDate.now() : LocalDate.parse(date));
        entry.setSystolic(systolic);
        entry.setDiastolic(diastolic);
        entry.setSugarLevelFasting(sugarLevelFasting);
        entry.setSugarLevelPostMeal(sugarLevelPostMeal);
        entryRepo.save(entry);

        return "redirect:/main?userId=" + userId;
    }

    @GetMapping({"/", "/main"})
    public String index(@RequestParam(required = false) Long userId, Model model, HttpSession session) {
        Boolean authenticated = (Boolean) session.getAttribute(AUTH_ATTR);
        if (authenticated == null || !authenticated) {
            return "redirect:/login";
        }

        var users = userRepo.findAll();
        User selected = null;
        if (userId != null) {
            selected = userRepo.findById(userId).orElse(null);
        }
        if (selected == null && !users.isEmpty()) {
            selected = users.get(0);
        }

        model.addAttribute("users", users);
        model.addAttribute("selected", selected);
        model.addAttribute("userId", selected == null ? null : selected.getId());

        Page<HealthEntry> entries = Page.empty();
        if (selected != null) {
            entries = entryRepo.findByUserId(selected.getId(), PageRequest.of(0, 50));
            if (entries == null) {
                entries = Page.empty();
            }
        }
        model.addAttribute("entries", entries);

        return "index";
    }
}
