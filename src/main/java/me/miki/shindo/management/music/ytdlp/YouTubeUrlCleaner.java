package me.miki.shindo.management.music.ytdlp;

public class YouTubeUrlCleaner {
    /**
     * Retorna a URL limpa apenas com o parâmetro "v", se for uma URL de playlist.
     * Caso contrário, retorna a URL original.
     */
    public static String cleanUrl(String url) {
        try {
            java.net.URL u = new java.net.URL(url);
            String base = u.getProtocol() + "://" + u.getHost() + u.getPath();

            String query = u.getQuery();
            if (query == null) return url;

            String[] params = query.split("&");
            String videoId = null;
            boolean isPlaylist = false;

            for (String param : params) {
                if (param.startsWith("v=")) {
                    videoId = param;
                }
                if (param.startsWith("list=") || param.startsWith("index=") || param.startsWith("start_radio=")) {
                    isPlaylist = true;
                }
            }

            // Se for uma playlist e tiver um vídeo válido, retorna só a URL do vídeo
            if (isPlaylist && videoId != null) {
                return base + "?" + videoId;
            }

            // Caso não seja playlist, retorna original
            return url;

        } catch (Exception e) {
            e.printStackTrace();
            return url;
        }
    }
}