package com.zxsoft.crawler.web.controller.dict;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zxsoft.crawler.entity.Location;
import com.zxsoft.crawler.web.service.website.DictService;

@Controller
@RequestMapping("/dict")
public class LocationController {

        @Autowired
        private DictService dictService;
        
        @ResponseBody
	@RequestMapping(value="location/ajax/{id}", method = RequestMethod.GET)
	public List<Location> index(@PathVariable(value = "id") int id, Model model) {
		
                List<Location> locations = dictService.getLocation(id);
		
		return locations;
	}
	
}
