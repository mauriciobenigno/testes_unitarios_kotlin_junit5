package com.mauriciobenigno.junit5demo

import org.junit.jupiter.api.*
import org.junit.jupiter.api.Test


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class Test {

    @BeforeAll
    fun runBeforeAll(){
        println("before all testes")
    }

    @BeforeEach
    fun runBefore(){
        println("before test")
    }

    @Test
    fun firstTest(){
        println("hello world")
    }

    @Test
    fun secondTest(){
        println("hello world from second")
    }

    @AfterEach
    fun runAfter(){
        println("afeter test ")
    }

    @AfterAll
    fun runAfterAll(){
        println("afeter all testes ")
    }
}