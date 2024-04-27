package com.ada.rotas.demotomtom.controller;

import com.ada.rotas.demotomtom.model.entrega.CadastroEntregaDTO;
import com.ada.rotas.demotomtom.model.entrega.Entrega;
import com.ada.rotas.demotomtom.model.entrega.ExibicaoEntregaDTO;
import com.ada.rotas.demotomtom.repository.EntregaRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("entregas")
public class EntregaController {

    @Autowired
    private EntregaRepository repository;

    public EntregaController(EntregaRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/{id}")
    public ResponseEntity buscarPorId(@PathVariable Long id) {
        var entrega = repository.getReferenceById(id);
        return ResponseEntity.ok(new ExibicaoEntregaDTO(entrega));
    }

    @GetMapping
    public ResponseEntity<Page<ExibicaoEntregaDTO>> listar(@PageableDefault(size = 10, sort = {"idEntrega"}) Pageable pageable) {
        var page = repository.findAll(pageable).map(ExibicaoEntregaDTO::new);
        return ResponseEntity.ok(page);
    }

    @PostMapping
    @Transactional
    public ResponseEntity cadastrar(@RequestBody @Valid CadastroEntregaDTO dados, UriComponentsBuilder uriBuilder) {
        var entrega = new Entrega(dados);
        repository.save(entrega);
        var uri = uriBuilder.path("/entregas/{id}").buildAndExpand(entrega.getIdEntrega()).toUri();
        return ResponseEntity.created(uri).body(new ExibicaoEntregaDTO(entrega));
    }

    @PutMapping
    @Transactional
    public ResponseEntity atualizar(@RequestBody @Valid Entrega atualizacao) {
        var entrega = repository.getReferenceById(atualizacao.getIdEntrega());
        entrega.atualizar(atualizacao);
        return ResponseEntity.ok(new ExibicaoEntregaDTO(entrega));
    }

    @PatchMapping
    @Transactional
    public ResponseEntity finalizarEntrega(@RequestBody @Valid Entrega atualizacao) {
        var entrega = repository.getReferenceById(atualizacao.getIdEntrega());
        entrega.finalizar();
        return ResponseEntity.ok(new ExibicaoEntregaDTO(entrega));
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity excluir(@PathVariable Long id) {
        var entrega = repository.getReferenceById(id);
        entrega.excluir();
        return ResponseEntity.noContent().build();
    }


}
