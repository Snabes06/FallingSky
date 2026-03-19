package sw.surasnipers.fallingsky.client.utils

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.concurrent.CompletableFuture

object HttpUtils {

    private val client = HttpClient.newBuilder().build()

    /** Sends an asynchronous GET request and returns a CompletableFuture with the response body. */
    fun fetchAsync(url: String): CompletableFuture<String> {
        val request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .GET()
            .build()

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
            .thenApply { it.body() }
    }
}
