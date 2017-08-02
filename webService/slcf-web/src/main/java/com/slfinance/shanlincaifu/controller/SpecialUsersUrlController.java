package com.slfinance.shanlincaifu.controller;

import com.slfinance.annotation.AutoDispatch;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.service.SpecialUsersUrlService;

import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Map;

/**
 * 特殊用户标的设置有效期
 *
 * @author  mali
 * @version $Revision:1.0.0, $Date: 2017年7月21日 下午11:16:12 $
 */
@RestController
@AutoDispatch(serviceInterface = {SpecialUsersUrlService.class})
@RequestMapping("specialUsersUrl")
public class SpecialUsersUrlController extends WelcomeController {

    @RequestMapping(value = "/{functionName:[a-zA-Z-]+}Auto", method=RequestMethod.POST)
    public @ResponseBody Object dipatch(HttpServletRequest request, HttpServletResponse response, @PathVariable("functionName") String name, @RequestBody Map<String, Object> model) throws SLException {
        return autoDispatch(request, response, name, model);
    }

    @RequestMapping(value = "/{functionName:[a-zA-Z-]+}Auto", method = RequestMethod.GET)
    public @ResponseBody Object dipatch2(HttpServletRequest request, HttpServletResponse response, @PathVariable("functionName") String name, @RequestParam Map<String, Object> model) throws SLException {
        return autoDispatch(request, response, name, model);
    }
}
