package com.example.BookShop.helpers;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;


@Controller
@RequestMapping("/image")
public class ImageUploads {
    @RequestMapping(value="getImage/{photo}",method = RequestMethod.GET)
    @ResponseBody
    @GetMapping("/getImage/{image}")
    public ResponseEntity<ByteArrayResource>getImage(@PathVariable("image" )String img){
        if(!img.equals("") || img!=null)
            try {
                Path filename = Paths.get("D://BookShop//uploads",img);
                byte[] buffer = Files.readAllBytes(filename);
                ByteArrayResource byteArrayResource = new ByteArrayResource(buffer);
                return ResponseEntity.ok()
                        .contentLength(buffer.length)
                        .contentType(MediaType.parseMediaType("image/png"))
                        .body(byteArrayResource);

            }catch (Exception e) {
                // TODO: handle exception
            }
        return ResponseEntity.badRequest().build();
    }

}
