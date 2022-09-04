package alexander.rest.controller;

import alexander.rest.services.ClientDAO;
import alexander.rest.model.Client;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v0/pool/client")
public class ClientController {

    private final ClientDAO clientDAO;

    @Autowired
    public ClientController(ClientDAO clientDAO) {
        this.clientDAO = clientDAO;
    }

    @GetMapping(value = "/all")
    public JsonArray getClients(Model model){
        return clientDAO.getClients();
    }

    @GetMapping("/get/{id}")
    public JsonObject getClient(@PathVariable("id") Number id){
        return clientDAO.getClient(id);
    }

    @PostMapping("/add")
    public void add(@RequestBody Client client){
        clientDAO.add(client);
    }

    @PostMapping("/update")
    public void addClient(@RequestBody Client client){
        clientDAO.update(client);
    }
}
