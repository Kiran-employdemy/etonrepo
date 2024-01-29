package com.eaton.platform.core.servlets.assets;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ServletInputStreamFixtures {
    public static ServletInputStream documentListWithoutSpecialCharacter() {
        return new ServletInputStreamFixture(ServletInputStreamFixtures.class.getResourceAsStream("documentListNoSpecialCharacters.json"));
    }

    public static ServletInputStream documentListWithSpecialCharacter() {
        return new ServletInputStreamFixture(ServletInputStreamFixtures.class.getResourceAsStream("documentListSpecialCharacters.json"));
    }

    public static ServletInputStream documentListEmptyProperty() {
        return new ServletInputStreamFixture(ServletInputStreamFixtures.class.getResourceAsStream("documentListEmptyProperty.json"));
    }

    public static ServletInputStream documentListNullProperty() {
        return new ServletInputStreamFixture(ServletInputStreamFixtures.class.getResourceAsStream("documentListNullProperty.json"));
    }

    static class ServletInputStreamFixture extends ServletInputStream {

        final InputStream inputStream;

        ServletInputStreamFixture(InputStream inputStream) {
            this.inputStream = inputStream;
        }


        @Override
        public boolean isFinished() {
            return false;
        }

        @Override
        public boolean isReady() {
            return false;
        }

        @Override
        public void setReadListener(ReadListener readListener) {

        }

        @Override
        public int read() throws IOException {
            return inputStream.read();
        }
    }

}
