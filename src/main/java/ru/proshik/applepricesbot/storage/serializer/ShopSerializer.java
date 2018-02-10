package ru.proshik.applepricesbot.storage.serializer;

import org.jetbrains.annotations.NotNull;
import org.mapdb.DataInput2;
import org.mapdb.DataOutput2;
import org.mapdb.Serializer;
import ru.proshik.applepricesbot.storage.model.Shop;

import java.io.IOException;
import java.io.Serializable;

public class ShopSerializer  implements Serializer<Shop>, Serializable {

    @Override
    public void serialize(@NotNull DataOutput2 out, @NotNull Shop value) throws IOException {
        out.writeUTF(value.getTitle());
        out.writeUTF(value.getUrl());
    }

    @Override
    public Shop deserialize(@NotNull DataInput2 input, int available) throws IOException {
        return new Shop(input.readUTF(), input.readUTF());
    }
}
