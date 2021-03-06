package com.example.demo.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigInteger;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "URLSTORE")
public class Url {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private BigInteger id;
        @Column
        private String hashvalue;
        @Column
        private String originurl;
        @Column
        private String name;
        @Column
        private Integer count=0;

        @Builder
        public Url(String hashvalue, String originurl, String name) {
                this.hashvalue = hashvalue;
                this.originurl = originurl;
                this.name = name;
        }
}