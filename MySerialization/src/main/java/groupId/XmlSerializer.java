package groupId;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.StringWriter;

public class XmlSerializer {
    public static String serialize(Object obj) {
        try {
            JAXBContext context = JAXBContext.newInstance(obj.getClass());
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            StringWriter writer = new StringWriter();
            marshaller.marshal(obj, writer);

            return writer.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error serializing object to XML: " + obj, e);
        }
    }

    public static <T> T deserialize(File file, Class<T> clazz) {
        try {
            JAXBContext context = JAXBContext.newInstance(clazz);
            Unmarshaller unmarshaller = context.createUnmarshaller();

            return clazz.cast(unmarshaller.unmarshal(file));
        } catch (Exception e) {
            throw new RuntimeException("Error deserializing XML file: " + file, e);
        }
    }
}
