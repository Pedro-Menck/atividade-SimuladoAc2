package com.seu.pacote;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.atividadeac2.R;

import java.util.List;
import java.util.Objects;

public class MediaAdapter extends ListAdapter<Media, MediaAdapter.MediaViewHolder> {

    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Media media);
        void onItemLongClick(Media media);
    }

    private static final DiffUtil.ItemCallback<Media> DIFF_CALLBACK = new DiffUtil.ItemCallback<>() {
        @Override
        public boolean areItemsTheSame(@NonNull Media oldItem, @NonNull Media newItem) {
            if (oldItem.getId() != null && newItem.getId() != null) {
                return oldItem.getId().equals(newItem.getId());
            }
            return oldItem == newItem || Objects.equals(oldItem.getNome(), newItem.getNome());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Media oldItem, @NonNull Media newItem) {
            return oldItem.getAno() == newItem.getAno()
                    && oldItem.getNota() == newItem.getNota()
                    && oldItem.isAssistido() == newItem.isAssistido()
                    && Objects.equals(oldItem.getNome(), newItem.getNome())
                    && Objects.equals(oldItem.getTipo(), newItem.getTipo())
                    && Objects.equals(oldItem.getGenero(), newItem.getGenero());
        }
    };

    public MediaAdapter(OnItemClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    public MediaAdapter(List<Media> mediaList, OnItemClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
        submitList(mediaList);
    }

    @NonNull
    @Override
    public MediaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_media, parent, false);
        return new MediaViewHolder(v, listener, this);
    }

    @Override
    public void onBindViewHolder(@NonNull MediaViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    public void updateList(List<Media> newList) {
        submitList(newList);
    }

    public Media getItemAt(int position) {
        return getItem(position);
    }

    public static class MediaViewHolder extends RecyclerView.ViewHolder {
        private final TextView txtNome;
        private final TextView txtDetalhes;

        public MediaViewHolder(@NonNull View itemView, OnItemClickListener listener, MediaAdapter adapter) {
            super(itemView);
            txtNome = itemView.findViewById(R.id.txtNomeItem);
            txtDetalhes = itemView.findViewById(R.id.txtDetalhesItem);

            itemView.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onItemClick(adapter.getItemAt(position));
                }
            });

            itemView.setOnLongClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onItemLongClick(adapter.getItemAt(position));
                    return true;
                }
                return false;
            });
        }

        public void bind(Media media) {
            txtNome.setText(media.getNome());

            String detalhes = media.getTipo() + " | "
                    + media.getGenero() + " | Ano: "
                    + media.getAno() + " | Nota: "
                    + media.getNota() + " | "
                    + (media.isAssistido() ? "Assistido" : "Não assistido");

            txtDetalhes.setText(detalhes);
        }
    }
}
