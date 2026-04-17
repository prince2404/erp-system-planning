import { ChevronLeft, ChevronRight } from 'lucide-react';
import EmptyState from './EmptyState.jsx';

export default function DataTable({ columns, rows, loading, emptyTitle, emptyDescription, page, totalPages, onPageChange }) {
  if (!loading && (!rows || rows.length === 0)) {
    return <EmptyState title={emptyTitle || 'No records found'} description={emptyDescription} />;
  }

  const getValue = (row, key) => {
    if (!key) return null;
    return key.split('.').reduce((obj, k) => obj?.[k], row);
  };

  return (
    <div className="animate-fade-in">
      <div className="overflow-x-auto">
        <table className="w-full text-left text-sm">
          <thead>
            <tr className="border-b border-ash-100">
              {columns.map(col => (
                <th key={col.key} className="whitespace-nowrap px-4 py-3 text-xs font-semibold uppercase tracking-wider text-ash-500">
                  {col.label}
                </th>
              ))}
            </tr>
          </thead>
          <tbody className="divide-y divide-ash-50">
            {rows?.map((row, i) => (
              <tr key={row.id || i} className="transition-colors hover:bg-ash-50/50">
                {columns.map(col => (
                  <td key={col.key} className="whitespace-nowrap px-4 py-3 text-ash-700">
                    {col.render ? col.render(row) : formatValue(getValue(row, col.key))}
                  </td>
                ))}
              </tr>
            ))}
          </tbody>
        </table>
      </div>
      {totalPages > 1 && (
        <div className="flex items-center justify-between border-t border-ash-100 px-4 py-3">
          <p className="text-xs text-ash-500">
            Page {(page || 0) + 1} of {totalPages}
          </p>
          <div className="flex gap-1">
            <button
              className="btn-icon btn-sm text-ash-500 hover:bg-ash-100"
              disabled={!page || page === 0}
              onClick={() => onPageChange?.(page - 1)}
            >
              <ChevronLeft size={16} />
            </button>
            <button
              className="btn-icon btn-sm text-ash-500 hover:bg-ash-100"
              disabled={page >= totalPages - 1}
              onClick={() => onPageChange?.(page + 1)}
            >
              <ChevronRight size={16} />
            </button>
          </div>
        </div>
      )}
    </div>
  );
}

function formatValue(value) {
  if (value === null || value === undefined) return <span className="text-ash-300">—</span>;
  if (typeof value === 'boolean') return value ? '✓' : '✗';
  return String(value);
}
