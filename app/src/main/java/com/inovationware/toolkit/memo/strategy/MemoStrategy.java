package com.inovationware.toolkit.memo.strategy;

import com.inovationware.toolkit.memo.entity.Memo;

import java.io.IOException;

public interface MemoStrategy {
    void saveNoteToCloud(Memo memo) throws IOException;

}
