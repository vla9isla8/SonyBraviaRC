package net.ddns.vla9isla8.ircc.service.bravia;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.ddns.vla9isla8.ircc.entity.Command;
import net.ddns.vla9isla8.ircc.service.IRCCService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Vladk on 26.11.2016.
 */
public class RemoteService implements IRCCService {

    private TVConnection tvConnection;

    private Map<String,String> defaultHeaders = new HashMap<>();

    private static ObjectMapper mapper = new ObjectMapper();

    public RemoteService(String IP, Path path) {
        this.tvConnection   =   new TVConnection(IP,path);
        // TODO: 26.11.2016 Сделать аутентификацию и получение следющих хедеров
        defaultHeaders.put("authorization", "Basic OjI1OTA=");
        defaultHeaders.put("SOAPAction", "urn:schemas-sony-com:service:IRCC:1#X_SendIRCC");
        defaultHeaders.put("Cookie", "auth=ad559218bc151f9ea66262d5839e4e43c3e2690710cbdb01809fd096345d9d33; path=/sony/; max-age=1209600; expires=Fri, 09-Dec-2016 17:41:58 GMT;");

    }
    private final String executiveCommandTemplate = "<?xml version=\"1.0\"?>\n" +
            "<s:Envelope xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\" s:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\n" +
            "  <s:Body>\n" +
            "    <u:X_SendIRCC xmlns:u=\"urn:schemas-sony-com:service:IRCC:1\">\n" +
            "      <IRCCCode>%s</IRCCCode>\n" +
            "    </u:X_SendIRCC>\n" +
            "  </s:Body>\n" +
            "</s:Envelope>";

    private final String commandListRequest = "{\"method\":\"getRemoteControllerInfo\",\"params\":[],\"id\":10, \"version\":\"1.0\"}\n";

    public List<Command> getCommandsList() {
        try (InputStream inputStream = tvConnection.makeRequest("system",new HashMap<>(defaultHeaders),"application/json",commandListRequest) ) {
            JsonNode node = mapper.readTree(inputStream);
            return mapper.convertValue(node.get("result").get(1), mapper.getTypeFactory().constructCollectionType(ArrayList.class, Command.class));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public void executeCommand(Command command,boolean printOutput) {
        try (InputStream inputStream = tvConnection.makeRequest("IRCC",new HashMap<>(defaultHeaders),"application/xml",String.format(executiveCommandTemplate,command.getValue()))) {
            if(printOutput){
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line = bufferedReader.readLine();
                while(line!=null){
                    System.out.println(line);
                    line = bufferedReader.readLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void disconnect() {
        tvConnection.closeConnection();
    }

    public enum Path{
        DEFAULT("/"),
        SONY("/sony/");

        private final String path;

        Path(String s) {
            this.path = s;
        }

        public String getPath() {
            return path;
        }
    }

}
