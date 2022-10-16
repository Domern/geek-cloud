package Netty.serial;

import model.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import java.nio.file.Files;
import java.nio.file.Path;


@Slf4j
public class FileHandler extends SimpleChannelInboundHandler<CloudMessage> {

    private Path serverDir;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        serverDir = Path.of("files");
        log.debug("client connected");
        ctx.writeAndFlush(new ListMessage(serverDir));
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, CloudMessage cloudMessage) throws Exception {
        log.debug("Received: {}", cloudMessage.getType());
        if (cloudMessage instanceof FileMessage fileMessage) {
            Files.write(serverDir.resolve(fileMessage.getFileName()), fileMessage.getBytes());
            ctx.writeAndFlush(new ListMessage(serverDir));
        } else if (cloudMessage instanceof FileRequest fileRequest) {
            ctx.writeAndFlush(new FileMessage(serverDir.resolve(fileRequest.getFileName())));
        } else if(cloudMessage instanceof DeleteFile deleteFile){
            Files.delete(serverDir.resolve(deleteFile.getDeleteFileName()));
            ctx.writeAndFlush(new ListMessage(serverDir));
        }else if (cloudMessage instanceof NewFileName newFileName){
            Files.copy(serverDir.resolve(newFileName.getLastFileName()),serverDir.resolve(newFileName.getNewFuleName()));
            Files.delete(serverDir.resolve(newFileName.getLastFileName()));
            ctx.writeAndFlush(new ListMessage(serverDir));
        }else if (cloudMessage instanceof GiveList giveList) {
            if(giveList.equals("..")){
                serverDir=serverDir.resolve("..");
                ctx.writeAndFlush(new ListMessage(serverDir));
            }else {
                Path dir = serverDir.resolve(giveList.getPath());
                if (Files.isDirectory(dir)) {
                    serverDir = serverDir.resolve(Path.of(giveList.getPath()));
                    ctx.writeAndFlush(new ListMessage(serverDir));
                }
            }
        }

    }


}