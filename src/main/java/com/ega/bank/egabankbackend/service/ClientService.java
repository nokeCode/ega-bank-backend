package com.ega.bank.egabankbackend.service;

import com.ega.bank.egabankbackend.dto.ClientDTO;
import com.ega.bank.egabankbackend.entity.Client;
import com.ega.bank.egabankbackend.exception.DuplicateResourceException;
import com.ega.bank.egabankbackend.exception.ResourceNotFoundException;
import com.ega.bank.egabankbackend.repository.ClientRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class ClientService {

    private ClientRepository clientRepository;

    /**
     * Créer un nouveau client
     */
    public ClientDTO createClient(ClientDTO clientDTO) {
        // Vérifier si l'email existe déjà
        if (clientRepository.existsByCourriel(clientDTO.getCourriel())) {
            throw new DuplicateResourceException(
                    "Un client avec l'email " + clientDTO.getCourriel() + " existe déjà");
        }

        // Vérifier si le numéro de téléphone existe déjà
        if (clientRepository.existsByNumeroTelephone(clientDTO.getNumeroTelephone())) {
            throw new DuplicateResourceException(
                    "Un client avec le numéro de téléphone " + clientDTO.getNumeroTelephone() + " existe déjà");
        }

        Client client = mapToEntity(clientDTO);
        Client savedClient = clientRepository.save(client);
        return mapToDTO(savedClient);
    }

    /**
     * Récupérer tous les clients
     */
    @Transactional(readOnly = true)
    public List<ClientDTO> getAllClients() {
        return clientRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupérer un client par ID
     */
    @Transactional(readOnly = true)
    public ClientDTO getClientById(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Client non trouvé avec l'ID : " + id));
        return mapToDTO(client);
    }

    /**
     * Récupérer un client par email
     */
    @Transactional(readOnly = true)
    public ClientDTO getClientByEmail(String email) {
        Client client = clientRepository.findByCourriel(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Client non trouvé avec l'email : " + email));
        return mapToDTO(client);
    }

    /**
     * Mettre à jour un client
     */
    public ClientDTO updateClient(Long id, ClientDTO clientDTO) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Client non trouvé avec l'ID : " + id));

        // Vérifier si l'email est modifié et s'il existe déjà
        if (!client.getCourriel().equals(clientDTO.getCourriel())
                && clientRepository.existsByCourriel(clientDTO.getCourriel())) {
            throw new DuplicateResourceException(
                    "Un client avec l'email " + clientDTO.getCourriel() + " existe déjà");
        }

        // Vérifier si le numéro de téléphone est modifié et s'il existe déjà
        if (!client.getNumeroTelephone().equals(clientDTO.getNumeroTelephone())
                && clientRepository.existsByNumeroTelephone(clientDTO.getNumeroTelephone())) {
            throw new DuplicateResourceException(
                    "Un client avec le numéro de téléphone " + clientDTO.getNumeroTelephone() + " existe déjà");
        }

        // Mise à jour des champs
        client.setNom(clientDTO.getNom());
        client.setPrenom(clientDTO.getPrenom());
        client.setDateNaissance(clientDTO.getDateNaissance());
        client.setSexe(clientDTO.getSexe());
        client.setAdresse(clientDTO.getAdresse());
        client.setNumeroTelephone(clientDTO.getNumeroTelephone());
        client.setCourriel(clientDTO.getCourriel());
        client.setNationalite(clientDTO.getNationalite());

        Client updatedClient = clientRepository.save(client);
        return mapToDTO(updatedClient);
    }

    /**
     * Supprimer un client
     */
    public void deleteClient(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Client non trouvé avec l'ID : " + id));
        clientRepository.delete(client);
    }

    /**
     * Mapper une entité Client vers un ClientDTO
     */
    private ClientDTO mapToDTO(Client client) {
        return ClientDTO.builder()
                .id(client.getId())
                .nom(client.getNom())
                .prenom(client.getPrenom())
                .dateNaissance(client.getDateNaissance())
                .sexe(client.getSexe())
                .adresse(client.getAdresse())
                .numeroTelephone(client.getNumeroTelephone())
                .courriel(client.getCourriel())
                .nationalite(client.getNationalite())
                .dateCreation(client.getDateCreation())
                .build();
    }

    /**
     * Mapper un ClientDTO vers une entité Client
     */
    private Client mapToEntity(ClientDTO dto) {
        return Client.builder()
                .nom(dto.getNom())
                .prenom(dto.getPrenom())
                .dateNaissance(dto.getDateNaissance())
                .sexe(dto.getSexe())
                .adresse(dto.getAdresse())
                .numeroTelephone(dto.getNumeroTelephone())
                .courriel(dto.getCourriel())
                .nationalite(dto.getNationalite())
                .build();
    }
}
