package ru.proshik.applepriceparcer.storage.serializer2;

import org.jetbrains.annotations.NotNull;
import org.mapdb.DataInput2;
import org.mapdb.DataOutput2;
import org.mapdb.Serializer;
import ru.proshik.applepriceparcer.model2.UserSubscriptions;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class UserSerializer implements Serializer<UserSubscriptions>, Serializable {

    @Override
    public void serialize(@NotNull DataOutput2 out, @NotNull UserSubscriptions value) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(out);
        oos.writeObject(value);
    }

    @Override
    public UserSubscriptions deserialize(@NotNull DataInput2 input, int available) throws IOException {
        try {
            ObjectInputStream in2 = new ObjectInputStream(new DataInput2.DataInputToStream(input));
            return (UserSubscriptions) in2.readObject();
        } catch (ClassNotFoundException e) {
            throw new IOException(e);
        }
    }
}
