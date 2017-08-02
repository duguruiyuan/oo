package com.slfinance.shanlincaifu.controller;

import com.slfinance.annotation.AutoDispatch;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.service.CalcCommService;
import com.slfinance.vo.ResultVo;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Created by Laurance on 2017/4/27.
 */
@RestController
@AutoDispatch(serviceInterface= {CalcCommService.class})
@RequestMapping("calcComm")
public class CalcCommController extends WelcomeController  {

    @RequestMapping("/{functionName:[a-zA-Z-]+}Auto")
    public @ResponseBody
    Object dipatch(HttpServletRequest request, HttpServletResponse response, @PathVariable("functionName") String name, @RequestBody Map<String, Object> model) throws SLException {
        return super.autoDispatch(request, response, name, model);
    }
    @RequestMapping(value="/{functionName:[a-zA-Z-]+}Auto", method=RequestMethod.GET)
    public @ResponseBody Object dipatch2(HttpServletRequest request, HttpServletResponse response, @PathVariable("functionName") String name, @RequestParam Map<String, Object> model) throws SLException{
        return autoDispatch(request, response, name, model);
    }

}
