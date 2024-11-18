package com.miotlink.bluetooth.command;
import androidx.annotation.NonNull;
import com.miotlink.bluetooth.utils.HexUtil;
import java.nio.ByteBuffer;
public class UnBindCommand extends AbsMessage{

    private int kindId=0;
    private int modelId=0;
    public UnBindCommand(int  kindId,int modelId){
       this.kindId=kindId;
       this.modelId=modelId;
    }


    @NonNull
    @Override
    public String toString() {
        ByteBuffer buffer = ByteBuffer.allocate(4+3);
        buffer.put((byte) 0x0C);
        buffer.put((byte) 0x01);
        buffer.putShort((short) kindId);
        buffer.putShort((short) modelId);
        byte[] array = buffer.array();
        return HexUtil.encodeHexStr(array);

    }
}
