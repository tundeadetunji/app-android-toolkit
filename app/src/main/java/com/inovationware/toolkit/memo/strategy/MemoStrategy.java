package com.inovationware.toolkit.memo.strategy;

import com.inovationware.toolkit.memo.model.Memo;

import java.io.IOException;

public interface MemoStrategy {
    void saveNoteToCloud(Memo memo) throws IOException;

}
