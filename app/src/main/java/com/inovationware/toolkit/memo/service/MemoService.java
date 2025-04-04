package com.inovationware.toolkit.memo.service;

import com.inovationware.toolkit.memo.model.Memo;

import java.io.IOException;

public interface MemoService {
    void saveNoteToCloud(Memo memo) throws IOException;
}
