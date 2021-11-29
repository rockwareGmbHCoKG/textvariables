package de.rockware.aem.token.impl.servlet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import javax.servlet.ServletOutputStream;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith({ AemContextExtension.class, MockitoExtension.class })
class TokenResponseWrapperTest {

    private final AemContext ctx = new AemContext();

    private TokenResponseWrapper wrapper;

    @BeforeEach
    void setUp() {
        ctx.load().json("/de/rockware/aem/token/api/resource/ExperiencePage.json", "/content");
        ctx.currentResource("/content/experiencepage");
        ctx.response().setCharacterEncoding("UTF-8");
        wrapper = new TokenResponseWrapper(ctx.response());
    }

    @Test
    void getOutputStream() throws IOException {
        ServletOutputStream outputStream = wrapper.getOutputStream();
        outputStream.isReady();
        outputStream.flush();
        outputStream.close();
        outputStream.write(5);
        outputStream.setWriteListener(null);
        wrapper.flushBuffer();
        assertThrows(IllegalStateException.class, () -> {
            wrapper.getWriter();
        });
    }

    @Test
    void getWriter() throws IOException {
        assertNotNull(wrapper.getWriter());
        wrapper.flushBuffer();
        assertThrows(IllegalStateException.class, () -> {
            wrapper.getOutputStream();
        });
    }

    @Test
    void getResponseAsBytesWriter() throws IOException {
        wrapper.getWriter();
        wrapper.getResponseAsBytes();
    }

    @Test
    void getResponseAsBytesOS() throws IOException {
        wrapper.getOutputStream();
        wrapper.getResponseAsBytes();
    }

    @Test
    void getResponseAsBytesNullWriter() throws IOException {
        wrapper.getResponseAsBytes();
    }

    @Test
    void getResponseAsString() throws IOException {
        wrapper.getResponseAsString();
    }
}