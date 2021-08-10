package org.example.cayenne.persistent.auto;

import org.apache.cayenne.BaseDataObject;
import org.apache.cayenne.exp.property.EntityProperty;
import org.apache.cayenne.exp.property.PropertyFactory;
import org.apache.cayenne.exp.property.StringProperty;
import org.example.cayenne.persistent.Artist;
import org.example.cayenne.persistent.Gallery;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Class _Painting was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _Painting extends BaseDataObject {

    private static final long serialVersionUID = 1L;

    public static final String ID_PK_COLUMN = "Id";

    public static final StringProperty<String> NAME = PropertyFactory.createString("name", String.class);
    public static final EntityProperty<Artist> ARTIST = PropertyFactory.createEntity("artist", Artist.class);
    public static final EntityProperty<Gallery> GALLERY = PropertyFactory.createEntity("gallery", Gallery.class);

    protected String name;

    protected Object artist;
    protected Object gallery;

    public void setName(String name) {
        beforePropertyWrite("name", this.name, name);
        this.name = name;
    }

    public String getName() {
        beforePropertyRead("name");
        return this.name;
    }

    public void setArtist(Artist artist) {
        setToOneTarget("artist", artist, true);
    }

    public Artist getArtist() {
        return (Artist)readProperty("artist");
    }

    public void setGallery(Gallery gallery) {
        setToOneTarget("gallery", gallery, true);
    }

    public Gallery getGallery() {
        return (Gallery)readProperty("gallery");
    }

    @Override
    public Object readPropertyDirectly(String propName) {
        if(propName == null) {
            throw new IllegalArgumentException();
        }

        switch(propName) {
            case "name":
                return this.name;
            case "artist":
                return this.artist;
            case "gallery":
                return this.gallery;
            default:
                return super.readPropertyDirectly(propName);
        }
    }

    @Override
    public void writePropertyDirectly(String propName, Object val) {
        if(propName == null) {
            throw new IllegalArgumentException();
        }

        switch (propName) {
            case "name":
                this.name = (String)val;
                break;
            case "artist":
                this.artist = val;
                break;
            case "gallery":
                this.gallery = val;
                break;
            default:
                super.writePropertyDirectly(propName, val);
        }
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        writeSerialized(out);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        readSerialized(in);
    }

    @Override
    protected void writeState(ObjectOutputStream out) throws IOException {
        super.writeState(out);
        out.writeObject(this.name);
        out.writeObject(this.artist);
        out.writeObject(this.gallery);
    }

    @Override
    protected void readState(ObjectInputStream in) throws IOException, ClassNotFoundException {
        super.readState(in);
        this.name = (String)in.readObject();
        this.artist = in.readObject();
        this.gallery = in.readObject();
    }

}