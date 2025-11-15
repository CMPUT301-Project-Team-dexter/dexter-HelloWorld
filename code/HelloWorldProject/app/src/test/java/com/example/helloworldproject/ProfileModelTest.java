package com.example.helloworldproject;

import com.example.helloworldproject.model.Profile;
import com.example.helloworldproject.model.UserGroup;

import org.junit.Test;

import java.io.*;

import static org.junit.Assert.*;

public class ProfileModelTest {

    @Test
    public void settersAndGetters_work() {
        Profile p = new Profile();
        p.setId("device-123");
        p.setDeviceId("device-123");
        p.setName("Alex");
        p.setEmail("alex@example.com");
        p.setPhone("123-456");
        p.setNotificationOptOut(Boolean.FALSE);

        assertEquals("device-123", p.getId());
        assertEquals("device-123", p.getDeviceId());
        assertEquals("Alex", p.getName());
        assertEquals("alex@example.com", p.getEmail());
        assertEquals("123-456", p.getPhone());
        assertFalse(p.getNotificationOptOut());
    }

    @Test
    public void isSerializable_roundTrip() throws Exception {
        Profile p = new Profile(
            "12345", "device-123",
            "Alex", "alex@example.com",
            "1234675", UserGroup.ENTRANT
        );

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(p);
        oos.close();

        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
        Profile copy = (Profile) ois.readObject();

        assertEquals(p.getDeviceId(), copy.getDeviceId());
        assertEquals(p.getName(), copy.getName());
        assertEquals(p.getEmail(), copy.getEmail());
        assertEquals(p.getPhone(), copy.getPhone());
    }
}
