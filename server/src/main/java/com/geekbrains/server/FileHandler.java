package com.geekbrains.server;


import com.geekbrains.core.model.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
public class FileHandler extends SimpleChannelInboundHandler<AbstractMessage> {

    private Path currentDir;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        currentDir = Paths.get("root");
        if (!Files.exists(currentDir)) {
            Files.createDirectory(currentDir);
        }

        log.debug("client connected");
        ctx.writeAndFlush(new FileListResponse(currentDir));
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, AbstractMessage abstractMessage) throws Exception {
        switch (abstractMessage.getType()) {
            case FILE_REQUEST:
                FileRequest request = (FileRequest) abstractMessage;
                Path file = currentDir.resolve(request.getFileName());
                ctx.writeAndFlush(new FileMessage(file));
                break;
            case FILE_RESPONSE:
                FileMessage fileMsg = (FileMessage) abstractMessage;
                Files.write(
                        currentDir.resolve(fileMsg.getFileName()),
                        fileMsg.getBytes()
                );
                ctx.writeAndFlush(new FileListResponse(currentDir));
                break;
            case DELETEFILE:
                DeleteFile deleteFile = (DeleteFile) abstractMessage;
                Files.delete(currentDir.resolve(deleteFile.getDeleteFileName()));
                ctx.writeAndFlush(new FileListResponse(currentDir));
                break;
            case GIVELIST:
                GiveFileList giveFileList = (GiveFileList) abstractMessage;
                if (giveFileList.equals("..")) {
                    currentDir = currentDir.resolve("..");
                    ctx.writeAndFlush(new FileListResponse(currentDir));
                } else {
                    Path dir = currentDir.resolve(giveFileList.getPath());
                    if (Files.isDirectory(dir)) {
                        currentDir = currentDir.resolve(Path.of(giveFileList.getPath()));
                        ctx.writeAndFlush(new FileListResponse(currentDir));
                    }
                }
                break;
            case NEWFILENAME:
                NewFileName newFileName = (NewFileName) abstractMessage;
                Files.copy(currentDir.resolve(newFileName.getLastFileName()), currentDir.resolve(newFileName.getNewFileName()));
                Files.delete(currentDir.resolve(newFileName.getLastFileName()));
                ctx.writeAndFlush(new FileListResponse(currentDir));
                break;
        }

    }


}