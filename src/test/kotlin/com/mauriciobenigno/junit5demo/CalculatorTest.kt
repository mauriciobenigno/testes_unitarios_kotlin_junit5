package com.mauriciobenigno.junit5demo

import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.ValueSource
import java.lang.IllegalArgumentException
import kotlin.random.Random


class CalculatorTest {


    private lateinit var calculator: Calculator

    @BeforeEach
    fun setUp(){
        calculator = Calculator()
    }

    @Nested
    @DisplayName("Sucesso em todos os casos para Calculator")
    inner class SucessCases {
        @Test
        fun `retorna sucesso ao calcular corretamente soma`(){

            // Giver
            val primeiro = 3
            val segundo = 5

            // When
            val sum = calculator.positiveSum(primeiro, segundo)

            // Then
            assertAll({
                assertTrue( sum > 0)
                assertEquals(8 , sum)
                assertFalse( sum <= 0)
                assertNotNull(sum)
            })
        }

        @ParameterizedTest(name= "{0} + {1} = {2}")
        @CsvSource("1,5,6", "7,3,10")
        fun `retorna sucesso ao calcular corretamente soma com parametro`(first: Int, second: Int, resultado: Int){

            // when
            val sum = calculator.positiveSum(first, second)

            // then
            assertEquals(resultado, sum)
        }

        @ParameterizedTest
        @ValueSource(ints = [1, 5 ,55 , 98])
        fun `retorna sucesso ao calcular com entrada 0`(input: Int){

            // when
            val sum = calculator.positiveSum(0, input)

            // then
            assertEquals(input, sum)
        }

        @TestFactory
        fun `test factory for x+100 tests`(): List<DynamicNode>{
            return List(10) { Random.nextInt(0,100) }
                .map {
                    dynamicTest("should add $it to 100 and return ${it+100}") {
                        assertEquals(100+it, calculator.positiveSum(it, 100))
                    }
                }
        }

    }

    @Nested
    @DisplayName("Falha em todos os casos para Calculator")
    inner class FailureCases {

        @Test
        fun `retorna excecao quando algum dos valores é negativo`(){

            // Giver
            val primeiro = -4
            val segundo = 5

            //when
            val executable = Executable { calculator.positiveSum(primeiro, segundo) }

            // Then
            assertThrows(IllegalArgumentException::class.java, executable)
        }
    }

}