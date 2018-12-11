package com.zplus.monitor.service.impl;

import com.sun.management.OperatingSystemMXBean;
import com.zplus.monitor.domain.Systmm;
import com.zplus.monitor.service.SystmmService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.*;
import java.lang.management.ManagementFactory;
import java.util.StringTokenizer;

@Service
public class SystmmServiceImpl implements SystmmService
{
    @Resource
    private Systmm systmm;
    
    @Override
    public Systmm getSystemInfo() throws Exception
    {
        String str;
        StringTokenizer tokenizer;
        Runtime rt=Runtime.getRuntime();
        Process p=rt.exec("uptime");
        InputStreamReader ir = new InputStreamReader(p.getInputStream());
        LineNumberReader input = new LineNumberReader(ir);
        //String result;
        while ((str = input.readLine()) != null){
            //result=str;
            tokenizer=new StringTokenizer(str,", ");
            while(tokenizer.hasMoreTokens())
            {
                str=tokenizer.nextToken();
                if(str.equalsIgnoreCase("up"))
                {
                    systmm.setServerRuntime(tokenizer.nextToken()+" "+tokenizer.nextToken()+" "+tokenizer.nextToken());
                }
                else if(str.equalsIgnoreCase("average:"))
                {
                    systmm.setLoadAverage(tokenizer.nextToken()+" "+tokenizer.nextToken()+" "+tokenizer.nextToken());
                }
            }
            
        }
        /*while((str=br.readLine())!=null)
        {
            System.out.println(str);
            tokenizer=new StringTokenizer(str,", ");
            if(!tokenizer.hasMoreTokens())
                continue;
            str=tokenizer.nextToken();
            System.out.println(str);
            if(str.equalsIgnoreCase("up"))
            {
                systmm.setServerRuntime(tokenizer.nextToken()+" "+tokenizer.nextToken()+" "+tokenizer.nextToken());
            }
            else if(str.equalsIgnoreCase("average:"))
            {
                systmm.setLoadAverage(tokenizer.nextToken()+" "+tokenizer.nextToken()+" "+tokenizer.nextToken());
            }
        }*/
        
        BufferedReader br;
        p=rt.exec("ifconfig -a");
        br=new BufferedReader(new InputStreamReader(p.getInputStream()));
        int l=0;
        while((str=br.readLine())!=null)
        {
            if(++l==3)
                break;
            tokenizer=new StringTokenizer(str);
            while(tokenizer.hasMoreTokens())
            {
                str=tokenizer.nextToken();
                if(str.equalsIgnoreCase("inet"))
                {
                    systmm.setIpAddress(tokenizer.nextToken());
                    break;
                }
            }
        }

        OperatingSystemMXBean osmxb = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        str=osmxb.getName()+" Version "+osmxb.getVersion();
        systmm.setServerVersion(str);
        
        return systmm;
    }
}