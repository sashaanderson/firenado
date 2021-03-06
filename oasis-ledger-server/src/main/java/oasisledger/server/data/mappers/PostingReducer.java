package oasisledger.server.data.mappers;

import oasisledger.server.data.dto.PostingDTO;
import oasisledger.server.data.dto.StatementDTO;
import org.jdbi.v3.core.result.LinkedHashMapRowReducer;
import org.jdbi.v3.core.result.RowView;

import java.util.Map;

public class PostingReducer implements LinkedHashMapRowReducer<Long, PostingDTO.Header> {
    @Override
    public void accumulate(Map<Long, PostingDTO.Header> map, RowView rowView) {
        PostingDTO.Header ph = map.computeIfAbsent(rowView.getColumn("posting_header_id", Long.class),
                id -> rowView.getRow(PostingDTO.Header.class));
        PostingDTO.Detail pd = rowView.getRow(PostingDTO.Detail.class);
        pd.setAmount(pd.getAmount().movePointLeft(2)); // scale = 2
        ph.getDetails().add(pd);

        Long sid = rowView.getColumn("statement_id", Long.class);
        if (sid != null && sid > 0) {
            StatementDTO s = rowView.getRow(StatementDTO.class);
            pd.setStatement(s);
        }
    }
}
