package ru.proshik.applepricebot.storage.serializer;

import org.jetbrains.annotations.NotNull;
import org.mapdb.DataInput2;
import org.mapdb.DataOutput2;
import org.mapdb.Serializer;
import ru.proshik.applepricebot.storage.model.Fetch;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;

public class FetchListSerializer implements Serializer<List<Fetch>>, Serializable {

    @Override
    public void serialize(@NotNull DataOutput2 out, @NotNull List<Fetch> value) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(out);
        oos.writeObject(value);
    }

    @Override
    public List<Fetch> deserialize(@NotNull DataInput2 input, int available) throws IOException {
        try {
            ObjectInputStream in2 = new ObjectInputStream(new DataInput2.DataInputToStream(input));
            return (List) in2.readObject();
        } catch (ClassNotFoundException e) {
            throw new IOException(e);
        }
    }
}
