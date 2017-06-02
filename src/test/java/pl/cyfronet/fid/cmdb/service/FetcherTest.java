package pl.cyfronet.fid.cmdb.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Sets;

import pl.cyfronet.fid.cmdb.WireMockTest;
import pl.cyfronet.fid.cmdb.service.Fetcher;

public class FetcherTest extends WireMockTest {

    @Autowired
    Fetcher fetcher;

    @Test
    public void testGetOnlyLeafId() throws Exception {
        Set<String> ids = fetcher.getItemAndDependentItems("image-delete1");

        assertThat(ids).isEqualTo(Sets.newHashSet("image-delete1"));
    }

    @Test
    public void testGetRootDependencies() throws Exception {
        Set<String> ids = fetcher.getItemAndDependentItems("provider-delete");

        assertThat(ids).isEqualTo(Sets.newHashSet("provider-delete", "service-delete1", "service-delete2",
                "service-delete3", "image-delete1", "image-delete2"));
    }

    @Test
    public void testGetRevisions() throws Exception {
        Map<String, String> revs = fetcher.getRevs(Arrays.asList("id1", "id2"));

        assertThat(revs.get("id1")).isEqualTo("id1-rev");
        assertThat(revs.get("id2")).isEqualTo("id2-rev");
    }
}
