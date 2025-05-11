package com.example.backend.controllers.membersController;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.entities.members.Members;
import com.example.backend.requests.addMembersRequest.AddMemberRequest;
import com.example.backend.services.membersService.MembersService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/members")
public class MembersController {

    @Autowired
    private MembersService membersService;

    @GetMapping("/get-members")
    public ResponseEntity<?> getMembers(){
        try{
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
            if(roles.get(0).equals("ROLE_OWNER")){
                List<Members> members = membersService.getMembers();
                return ResponseEntity.ok().body(Map.of("members", members));
            }
            return ResponseEntity.ok().body(Map.of("members", "members"));

        } catch(Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("add-member")
    public ResponseEntity<?> addMember(HttpServletRequest request, @RequestBody @Valid AddMemberRequest addMemberRequest) {
        try{
            membersService.addMember(addMemberRequest);
            return ResponseEntity.ok().body(Map.of("message", "Member added successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                     .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                     .body(Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/delete-member")
    public ResponseEntity<?> deleteMember(@RequestParam String memberId) {
        try{
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

            membersService.deleteMember(memberId,roles.get(0));

            return ResponseEntity.ok().body(Map.of("message", "Member deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", e.getMessage()));
        }   
    }
}
