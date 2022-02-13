package com.mauriciobenigno.junit5demo

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import java.util.*

@ExtendWith(MockitoExtension::class)
internal class PostServiceTest{

    @InjectMocks
    private lateinit var postService: PostService

    @Mock
    private lateinit var postDatabase: PostDatabase

    @Mock
    private lateinit var userDatabase: UserDatabase

    @Mock
    private lateinit var authDatabase: AuthDatabase

    @Mock
    private lateinit var errorLogger: ErrorLogger

    /*@BeforeEach
    fun setUp(){
        postDatabase = mock<PostDatabase>()
        userDatabase = mock()
        authDatabase = mock()
        errorLogger = mock()

        postService = PostService(postDatabase, userDatabase, authDatabase, errorLogger)
    }*/

    @Test
    fun `deve criar uma instancia nao nula dos servicos e mock`(){
        assertAll({
            assertNotNull(postService)
            assertNotNull(postDatabase)
            assertNotNull(userDatabase)
            assertNotNull(authDatabase)
            assertNotNull(errorLogger)
        })
    }

    @Test
    fun `deve permitir um usuario writer crie posts`(){
        val userId = "111"
        val writer = User(userId,User.UserRole.WRITER, "John", "john@example.com")
        val post = Post("Hello", "Hello World post", null, null)


        `when`(userDatabase.loadUserById("111")).thenReturn(writer)
        whenever(authDatabase.isAllowedToWritePostos(writer)).thenReturn(true)

        val result = postService.createPost("111", post)

        assertTrue(result)
        assertEquals(writer, post.createdBy)

        verify(userDatabase).loadUserById(userId)
        verify(authDatabase).isAllowedToWritePostos(writer)
        verify(postDatabase).savePost(post)
    }

    @Test
    fun `deve proibir que usuarios reader criem posts`(){
        val reader = User(UUID.randomUUID().toString(),User.UserRole.READER, "John", "john@example.com")
        val post = Post("Hello", "Hello World post", null, null)


        whenever(userDatabase.loadUserById(anyString())).thenReturn(reader)
        whenever(authDatabase.isAllowedToWritePostos(any())).thenReturn(false)

        val result = postService.createPost("", post)

        assertFalse(result)
        assertNull(post.createdBy)
        assertNull(post.createdAt)

        verify(userDatabase).loadUserById(anyString())
        verify(authDatabase).isAllowedToWritePostos(any())
        verifyNoMoreInteractions(userDatabase, authDatabase)
        verifyNoInteractions(postDatabase)
    }

    @Test
    fun `deve registrar log de erro quando o usuario nao for encontrado `() {

        // given
        val post = Post("Hello", "World", null, null)
        val userId = UUID.randomUUID().toString()

        //when
        doThrow(UserNotFoundException("user not found")).`when`(userDatabase).loadUserById(any())
        val captor = argumentCaptor<Error>()

        // then
        val result = postService.createPost(userId, post)

        assertFalse(result)
        verify(errorLogger).logError(captor.capture())
        assertEquals(Error.ErrorType.DATABASE_ERROR, captor.firstValue.type)
        assertEquals("Could not load user $userId", captor.firstValue.message)
    }

}