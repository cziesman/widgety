package com.redhat.widget.ui;

import com.redhat.widget.rest.Widget;
import com.redhat.widget.rest.WidgetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.persistence.PersistenceException;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;

@Controller
@RequestMapping("/web")
public class WidgetWebController {

    private static final Logger LOG = LoggerFactory.getLogger(WidgetWebController.class);

    @Autowired
    private WidgetService widgetService;

    @GetMapping({
            "/index", "/", ""
    })
    public String index() {

        return "redirect:/web/widgets";
    }

    @GetMapping("/widgets")
    public String findAll(Model model) {

        model.addAttribute("widgets", widgetService.findWidgets());

        return "widget-list";
    }

    @GetMapping("/widget")
    public String add(Model model) {

        model.addAttribute("widget", new Widget());

        return "widget-add";
    }

    @PostMapping(value = "widget", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String add(@Valid Widget widget, BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            LOG.debug(widget.toString());
            model.addAttribute("widget", widget);

            return "widget-add";
        }

        try {
            widgetService.saveOrUpdateWidget(widget);

            model.addAttribute("widgets", widgetService.findWidgets());

            return "redirect:/web/widgets";
        } catch (PersistenceException | ConstraintViolationException ex) {
            LOG.warn(ex.getMessage(), ex);
            model.addAttribute("widget", widget);

            return "widget-add";
        }
    }

    @GetMapping("widget/{widgetid}")
    public String update(@PathVariable("widgetid") Long widgetid, Model model) {

        Widget widget = widgetService.findById(widgetid);
        LOG.debug(widget.toString());
        model.addAttribute("widget", widget);

        return "widget-update";
    }

    @PostMapping(value = "widget/{widgetid}", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String update(@PathVariable("widgetid") Long widgetid, @Valid Widget widget, BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("widget", widget);

            return "widget-update";
        }

        Widget update = widgetService.findById(widgetid);
        update.setDescription(widget.getDescription());
        update.setName(widget.getName());

        try {
            widgetService.saveOrUpdateWidget(update);
            model.addAttribute("widgets", widgetService.findWidgets());

            return "redirect:/web/widgets";
        } catch (PersistenceException | ConstraintViolationException ex) {
            LOG.warn(ex.getMessage(), ex);
            model.addAttribute("widget", widget);

            return "widget-update";
        }
    }

    @GetMapping("widget/delete/{widgetid}")
    public String delete(@PathVariable("widgetid") Long widgetid, Model model) {

        Widget widget = widgetService.findById(widgetid);

        try {
            widgetService.deleteWidget(widget);
        } catch (PersistenceException ex) {
            LOG.warn(ex.getMessage(), ex);
        }
        model.addAttribute("widgets", widgetService.findWidgets());

        return "widget-list";
    }
}
