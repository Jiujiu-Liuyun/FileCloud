package com.zhangyun.filecloud.server.database.controller;


import com.zhangyun.filecloud.server.database.entity.FileChangeRecord;
import com.zhangyun.filecloud.server.database.service.FileChangeRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author zy
 * @since 2022-11-16
 */
@RestController
@RequestMapping("/file-transfer")
public class FileChangeRecordController {
    @Autowired
    private FileChangeRecordService fileChangeRecordService;

    @PostMapping("/insert")
    public boolean insert(@RequestBody FileChangeRecord fileChangeRecord) {
        return fileChangeRecordService.insertOne(fileChangeRecord);
    }

    @GetMapping("/delete")
    public boolean delete(@RequestParam Integer id) {
        return fileChangeRecordService.deleteById(id);
    }

}

