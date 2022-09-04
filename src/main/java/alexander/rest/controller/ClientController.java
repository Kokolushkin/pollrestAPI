package alexander.rest.controller;

import alexander.rest.services.ClientService;
import alexander.rest.model.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v0/pool/client")
public class ClientController {

    private final ClientService clientService;

    @Autowired
    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping(value = "/all")
    public String getClients(Model model){
        return clientService.getClients().toString();
    }

    @GetMapping("/get/{id}")
    public String getClient(@PathVariable("id") Number id){
        return clientService.getClient(id).toString();
    }

    @PostMapping("/add")
    public void add(@RequestBody Client client){
        clientService.add(client);
    }

    @PostMapping("/update")
    public void addClient(@RequestBody Client client){
        clientService.update(client);
    }
}
