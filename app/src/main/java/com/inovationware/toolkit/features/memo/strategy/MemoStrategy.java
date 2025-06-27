package com.inovationware.toolkit.features.memo.strategy;

import com.inovationware.toolkit.features.memo.model.Memo;

import java.io.IOException;

public interface MemoStrategy {
    void saveNoteToCloud(Memo memo) throws IOException;

}
